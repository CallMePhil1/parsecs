package com.github.callmephil1.parsecs.ecs.system

object Systems {
    private val monitorSystems: MutableList<MonitorSystem> = mutableListOf()
    private val renderSystems: MutableList<RenderSystem> = mutableListOf()
    private val updateSystems: MutableList<UpdateSystem> = mutableListOf()

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

    internal fun engineLoopEnd() {
        monitorSystems.forEach { it.engineLoopEnd() }
    }

    internal fun engineLoopStart() {
        monitorSystems.forEach { it.engineLoopStart() }
    }

    internal fun monitorSystemRenderLoop(delta: Float) {
        monitorSystems.forEach {
            it.draw(delta)
        }
    }

    internal fun monitorSystemUpdateLoop(delta: Float) {
        monitorSystems.forEach {
            it.update(delta)
        }
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