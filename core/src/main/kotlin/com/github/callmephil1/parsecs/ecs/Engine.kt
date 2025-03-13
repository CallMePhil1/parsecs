package com.github.callmephil1.parsecs.ecs

import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import com.github.callmephil1.parsecs.ecs.system.SystemBuilder
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

        entityService.releaseEntities()
        entityService.updateEntities()
        entityService.createEntities()

        systemService.updateSystemLoop(delta)

        systemService.renderSystemLoop(delta)

        systemService.engineLoopEnd()
    }

    fun update() {
        val deltaTime = stopWatch.seconds / 1000
        stopWatch.start()
        update(deltaTime)
        stopWatch.stop()
    }

    class Builder {
        private val diBuilder = DependencyInjection.Builder()
        private val systemBuilder = SystemBuilder()

        fun build(): Engine {
            diBuilder.singleton(EntityService::class.java)
            diBuilder.singleton(ComponentService::class.java)

            val systemsList = systemBuilder.build()
            systemsList.forEach {
                diBuilder.singleton(it)
            }

            val di = diBuilder.build()
            val entityService = di.get(EntityService::class.java)

            val systems = systemsList.map {
                di.get(it) as com.github.callmephil1.parsecs.ecs.system.System
            }
            val systemService = SystemService(systems)

            val engine = Engine(
                di,
                entityService,
                systemService
            )

            systemService.engineInitialized()

            return engine
        }

        fun services(configure: DependencyInjection.Builder.() -> Unit) {
            diBuilder.configure()
        }

        fun systems(configure: SystemBuilder.() -> Unit) {
            systemBuilder.configure()
        }
    }
}

fun engine(configure: Engine.Builder.() -> Unit): Engine {
    val builder = Engine.Builder()
    builder.configure()
    return builder.build()
}