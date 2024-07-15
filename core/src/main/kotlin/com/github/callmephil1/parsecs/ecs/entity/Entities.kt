package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.Engine
import com.github.callmephil1.parsecs.ecs.component.Components

object Entities {
    private val logger = System.getLogger(Entities::class.qualifiedName)

    private var availableEntityCount = 1
    private var entityCursor = -1
    internal var tail = 0

    internal var inUse = Array(1) { false }

    internal fun entitiesInUse(): List<EntityID> {
        val inUseCount = inUse.count { it }
        return ArrayList(inUseCount)
    }

    internal inline fun forEach(block: (EntityID) -> Unit) {
        try {
            for (i in 0 until tail) {
                if (inUse[i]) {
                    block(i)
                }
            }
        } catch (e: Exception) {
            println()
        }
    }

    fun getUnusedEntityID(): Int {
        if (availableEntityCount <= 0) {
            entityCursor = inUse.size - 1
            Engine.resize()
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
            tail = if (entityCursor > tail) entityCursor else tail
            return entityCursor
        }

        entityCursor = -1
        return getUnusedEntityID()
    }

    fun hardCompact() {
        val inUseCount = inUse.count { it }

        var head = 0
        var tail = 0

        while(tail < inUseCount) {
            if (inUse[tail]) {
                inUse[head] = inUse[tail]
                head += 1
            }
            tail += 1
        }

        val resizeSize = if (inUseCount <= 0) 1 else inUseCount
        resize(resizeSize)
    }

    fun softCompact() {
        for(i in inUse.indices.reversed()) {
            if (inUse[i]) {
                tail = i
                entityCursor = if (entityCursor > tail) 0 else entityCursor
                return
            }
        }
        tail = 0
        entityCursor = 0
    }

    fun releaseEntityID(entityID: EntityID) {
        inUse[entityID] = false
        availableEntityCount += 1
    }

    fun removeAllEntities() {
        for(i in 0 until tail) {
            inUse[i] = false
        }
        Components.removeAllComponents(tail)
    }

    internal fun resize(newSize: Int = -1) {
        val size = when {
            newSize < 0 -> inUse.size * 2
            newSize == 0 -> 1
            else -> newSize
        }

        logger.log(System.Logger.Level.DEBUG) { "Resizing entities array to a size of $size" }
        inUse = Array(size) {
            if (it < inUse.size) inUse[it]
            else false
        }

        entityCursor = if (entityCursor >= size) 0 else entityCursor
        availableEntityCount = inUse.count { it }
        tail = size - 1
    }
}