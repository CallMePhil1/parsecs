package com.github.callmephil1.parsecs.ecs.entity

class EntityRef(val entity: Entity) {
    val version: UInt = entity.version

    val isAlive get() = version == entity.version

    companion object {
        val EMPTY = EntityRef(Entity()).apply { entity.version = 1u }
    }
}