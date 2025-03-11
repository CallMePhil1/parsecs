package com.github.callmephil1.parsecs.ecs.entity.dsl

import com.github.callmephil1.parsecs.ecs.entity.Entities
import com.github.callmephil1.parsecs.ecs.entity.Entity
import com.github.callmephil1.parsecs.ecs.entity.EntityService

interface EntityDsl {
    val entityService: EntityService

    fun entities(configure: Entities.Builder.() -> Unit): Entities = entityService.entities(configure)

    fun entity(configure: Entity.() -> Unit): Entity = entityService.entity(configure)

    fun Entity.update(block: Entity.() -> Unit) {
        this.block()
        entityService.update(this)
    }
}
