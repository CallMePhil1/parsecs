package parsecs.ecs.system

interface System {
    fun addedToEngine() {}
    fun removedFromEngine() {}
}

interface MonitorSystem: RenderSystem, UpdateSystem {
    fun monitorSystemAdded(system: MonitorSystem)
    fun renderSystemAdded(system: RenderSystem)
    fun updateSystemAdded(system: UpdateSystem)

    fun engineLoopStart()
    fun engineLoopEnd()

    fun systemLoopStart(system: System)
    fun systemLoopEnd(system: System)
}

interface RenderSystem: System {
    fun draw(delta: Float)
}

interface UpdateSystem: System {
    fun update(delta: Float)
}