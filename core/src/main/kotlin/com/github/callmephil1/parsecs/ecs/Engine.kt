package com.github.callmephil1.parsecs.ecs

import com.github.callmephil1.parsecs.ecs.entity.EntityService
import com.github.callmephil1.parsecs.ecs.system.SystemService

class Engine internal constructor(
    private val di: DependencyInjection,
    private val entityService: EntityService,
    private val systemService: SystemService
){
    private val stopWatch = StopWatch()
    private val logger = System.getLogger(Engine::class.qualifiedName)

    fun update(delta: Float) {
        systemService.engineLoopStart()

        entityService.createEntities()
        entityService.releaseEntities()

        systemService.updateSystemLoop(delta)

        systemService.renderSystemLoop(delta)

        systemService.engineLoopEnd()
    }

    fun update() {
        val deltaTime = stopWatch.seconds
        stopWatch.start()
        update(deltaTime)
        stopWatch.stop()
    }
}