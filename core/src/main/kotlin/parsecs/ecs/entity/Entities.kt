package parsecs.ecs.entity

import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ecs.component.Components

object Entities {
    private val logger = KotlinLogging.logger {}

    private var availableEntityCount = 0
    private var entityCursor = -1

    internal var inUse = Array(0) { false }

    fun getUnusedEntityID(): Int {
        if (availableEntityCount <= 0) {
            entityCursor = inUse.size - 1
            resize()
        }

        while (entityCursor < inUse.size) {
            entityCursor += 1

            if (inUse[entityCursor])
                continue

            inUse[entityCursor] = true
            availableEntityCount -= 1
            return entityCursor
        }

        entityCursor = -1
        return getUnusedEntityID()
    }

    internal fun resize(newSize: Int = -1) {
        val size = if (newSize < 0) inUse.size * 2 else newSize

        logger.debug { "Resizing entities array to a size of $size" }
        inUse = Array(size) {
            if (it < inUse.size) inUse[it]
            else false
        }

        Components.resize(size)
    }
}