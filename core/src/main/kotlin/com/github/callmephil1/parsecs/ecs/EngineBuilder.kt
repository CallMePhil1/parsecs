package com.github.callmephil1.parsecs.ecs

import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import com.github.callmephil1.parsecs.ecs.system.System
import com.github.callmephil1.parsecs.ecs.system.SystemService

class EngineBuilder {
    private val diBuilder = DependencyInjectionBuilder()
    private val systems = mutableListOf<Class<*>>()

    fun <T> addFactory(service: Class<T>, factory: DependencyInjection.() -> T) {

    }

    fun addScoped(service: Class<*>): EngineBuilder {
        diBuilder.scoped(service)
        return this
    }

    fun addSingleton(service: Class<*>): EngineBuilder {
        diBuilder.singleton(service)
        return this
    }

    fun <T: System> addSystem(system: Class<T>): EngineBuilder {
        diBuilder.singleton(system)
        systems.add(system)
        return this
    }

    fun build(): Engine {
        diBuilder.singleton(EntityService::class.java)
        diBuilder.singleton(ComponentService::class.java)

        val di = diBuilder.build()
        val entityService = di.get(EntityService::class.java)

        val systems = systems.map {
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
}