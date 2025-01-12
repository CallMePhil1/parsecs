package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.collections.Bag
import com.github.callmephil1.parsecs.ecs.collections.Bits
import com.github.callmephil1.parsecs.ecs.component.ComponentService

class Entities internal constructor(
    private val allMask: Bits?,
    private val anyMask: Bits?,
    private val noneMask: Bits?,
    val name: String
) : Iterable<Entity> {

    private val entities = Bag<Entity>()

    internal fun add(entity: Entity) = entities.add(entity)

    internal fun isInterested(entityBits: Bits) = (allMask == null || entityBits.contains(allMask)) &&
            (anyMask == null || entityBits.intersects(anyMask)) &&
            (noneMask == null || !entityBits.intersects(noneMask))

    internal fun remove(entity: Entity) = entities.remove(entity)

    override fun iterator(): Iterator<Entity> = entities.asSequence().filterNotNull().iterator()

    class Builder internal constructor(
        private val componentService: ComponentService,
        private val entityService: EntityService
    ) {
        private var allMask: Bits? = null
        private var anyMask: Bits? = null
        private var noneMask: Bits? = null
        private var name: String = ""

        fun build(): Entities {
            val newEntities = Entities(allMask, anyMask, noneMask, name)
            entityService.addEntities(newEntities)
            return newEntities
        }

        fun all(vararg components: Class<*>) {
            if (allMask == null)
                allMask = Bits()

            for (i in components.indices) {
                val index = componentService.getComponentIndex(components[i])
                allMask!!.setBit(index, true)
            }
        }

        fun any(vararg components: Class<*>) {
            if (anyMask == null)
                anyMask = Bits()

            for (i in components.indices) {
                val index = componentService.getComponentIndex(components[i])
                anyMask!!.setBit(index, true)
            }
        }

        fun name(name: String) {
            this.name = name
        }

        fun none(vararg components: Class<*>) {
            if (noneMask == null)
                noneMask = Bits()

            for (i in components.indices) {
                val index = componentService.getComponentIndex(components[i])
                noneMask!!.setBit(index, true)
            }
        }
    }
}