package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.component.Components

object Entities {
    private val logger = System.getLogger(Entities::class.qualifiedName)

    private var availableEntityCount = 1
    private var entityCursor = -1

    internal var inUse = Array(1) { false }

    fun getUnusedEntityID(): Int {
        if (availableEntityCount <= 0) {
            entityCursor = inUse.size - 1
            resize()
            availableEntityCount = entityCursor + 1
        }

        while (entityCursor < inUse.size - 1) {
            entityCursor += 1

            if (entityCursor == inUse.size) {
                println()
            }

            if (inUse[entityCursor])
                continue

            inUse[entityCursor] = true
            availableEntityCount -= 1
            return entityCursor
        }

        entityCursor = -1
        return getUnusedEntityID()
    }

    fun releaseEntityID(entityID: EntityID) {
        inUse[entityID] = false
        availableEntityCount += 1
    }

    internal fun resize(newSize: Int = -1) {
        val size = if (newSize < 0) inUse.size * 2 else newSize

        logger.log(System.Logger.Level.DEBUG) { "Resizing entities array to a size of $size" }
        inUse = Array(size) {
            if (it < inUse.size) inUse[it]
            else false
        }

        Components.resize(size)
    }
}