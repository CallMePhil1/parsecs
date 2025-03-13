package com.github.callmephil1.parsecs.ecs.system

internal class SystemService internal constructor(
    systems: List<System>
) {
    private val monitorSystems: MutableList<MonitorSystem> = mutableListOf()
    private val renderSystems: MutableList<RenderSystem> = mutableListOf()
    private val updateSystems: MutableList<UpdateSystem> = mutableListOf()

    init {
        systems.forEach {
            addSystem(it)
        }
    }

    private fun addSystem(system: System) {
        if (system is MonitorSystem)
            monitorSystems.add(system)

        if (system is RenderSystem)
            renderSystems.add(system)

        if (system is UpdateSystem)
            updateSystems.add(system)
    }

    internal fun engineInitialized() {
        mutableSetOf<System>().also {
            it.addAll(monitorSystems)
            it.addAll(renderSystems)
            it.addAll(updateSystems)
        }.forEach {
            it.engineInitialized()
        }
    }

    internal fun engineLoopEnd() {
        monitorSystems.forEach { it.engineLoopEnd() }
    }

    internal fun engineLoopStart() {
        monitorSystems.forEach { it.engineLoopStart() }
    }

    internal fun renderSystemLoop(delta: Float) {
        renderSystems.forEach { renderer ->
            monitorSystems.forEach { it.systemLoopStart(renderer) }
            renderer.draw(delta)
            monitorSystems.forEach { it.systemLoopEnd(renderer) }
        }
    }

    internal fun updateSystemLoop(delta: Float) {
        updateSystems.forEach { updater ->
            monitorSystems.forEach { it.systemLoopStart(updater) }
            updater.update(delta)
            monitorSystems.forEach { it.systemLoopEnd(updater) }
        }
    }
}