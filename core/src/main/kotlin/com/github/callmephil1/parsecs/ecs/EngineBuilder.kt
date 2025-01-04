package com.github.callmephil1.parsecs.ecs

import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import com.github.callmephil1.parsecs.ecs.system.System
import com.github.callmephil1.parsecs.ecs.system.SystemBuilder
import com.github.callmephil1.parsecs.ecs.system.SystemService

class EngineBuilder {
    private val diBuilder = DependencyInjectionBuilder()
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
            di.get(it) as System
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

    fun services(configure: DependencyInjectionBuilder.() -> Unit) {
        diBuilder.configure()
    }

    fun systems(configure: SystemBuilder.() -> Unit) {
        systemBuilder.configure()
    }
}

fun engine(configure: EngineBuilder.() -> Unit): Engine {
    val builder = EngineBuilder()
    builder.configure()
    return builder.build()
}