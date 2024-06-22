package parsecs

import parsecs.ext.SystemExt.nanoToSeconds
import java.lang.System as JavaSystem
import parsecs.system.MonitorSystem
import parsecs.system.RenderSystem
import parsecs.system.System
import parsecs.system.UpdateSystem

object Engine {
    private var previousNanoSeconds: Long = 0
    private var deltaRenderTime: Float = 0f
    private var deltaUpdateTime: Float = 0f
    private var targetRenderTime: Float = 0f
    private var targetUpdateTime: Float = 0f
    private val monitorSystems: MutableList<MonitorSystem> = mutableListOf()
    private val renderSystems: MutableList<RenderSystem> = mutableListOf()
    private val updateSystems: MutableList<UpdateSystem> = mutableListOf()

    var fpsLimit: Int = 60
        set(value) {
            targetRenderTime = if (value == 0) 0f else 1f / value
            field = value
        }

    var updateLimit: Int = 0
        set(value) {
            targetUpdateTime = if (value == 0) 0f else 1f / value
            field = value
        }

    private fun addSystem(system: MonitorSystem) {
        monitorSystems.forEach { it.monitorSystemAdded(system) }
        monitorSystems.add(system)
        system.addedToEngine()

        renderSystems.forEach { system.renderSystemAdded(it) }
        updateSystems.forEach { system.updateSystemAdded(it) }
    }

    private fun addSystem(system: RenderSystem) {
        renderSystems.add(system)
        system.addedToEngine()
        monitorSystems.forEach { it.renderSystemAdded(system) }
    }

    private fun addSystem(system: UpdateSystem) {
        updateSystems.add(system)
        system.addedToEngine()
        monitorSystems.forEach { it.updateSystemAdded(system) }
    }

    fun addSystem(system: System) {
        if (system is MonitorSystem) {
            addSystem(system)
            return
        }

        if (system is RenderSystem)
            addSystem(system)

        if (system is UpdateSystem)
            addSystem(system)

        system.addedToEngine()
    }

    fun update() {
        monitorSystems.forEach { it.engineLoopStart() }

        if (previousNanoSeconds == 0L)
            previousNanoSeconds = java.lang.System.nanoTime()

        val currentNanoSeconds = JavaSystem.nanoTime()
        val timeDiff = currentNanoSeconds - previousNanoSeconds
        val delta = timeDiff.nanoToSeconds()

        deltaRenderTime += delta
        deltaUpdateTime += delta

        if (deltaUpdateTime >= targetUpdateTime) {
            updateSystems.forEach { updater ->
                monitorSystems.forEach { it.systemLoopStart(updater) }
                updater.update(deltaUpdateTime)
                monitorSystems.forEach { it.systemLoopEnd(updater) }
            }
        }

        if (deltaRenderTime >= targetRenderTime) {
            renderSystems.forEach { renderer ->
                monitorSystems.forEach { it.systemLoopStart(renderer) }
                renderer.draw(deltaRenderTime)
                monitorSystems.forEach { it.systemLoopEnd(renderer) }
            }
        }

        monitorSystems.forEach { it.engineLoopEnd() }

        monitorSystems.forEach {
            it.update(deltaUpdateTime)
        }

        monitorSystems.forEach {
            it.draw(deltaRenderTime)
        }

        if (deltaUpdateTime >= targetUpdateTime)
            deltaUpdateTime = 0f

        if (deltaRenderTime >= targetRenderTime)
            deltaRenderTime = 0f

        previousNanoSeconds = currentNanoSeconds
    }
}