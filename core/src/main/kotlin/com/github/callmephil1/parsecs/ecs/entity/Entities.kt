package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.collections.Bag
import com.github.callmephil1.parsecs.ecs.collections.Bits
import com.github.callmephil1.parsecs.ecs.component.ComponentService

typealias EntitiesListener = (Entity) -> Unit

class Entities internal constructor(
    private val allMask: Bits?,
    private val anyMask: Bits?,
    private val noneMask: Bits?,
    private var entityAdded: EntitiesListener?,
    private var entityRemoved: EntitiesListener?,
    private var entityUpdated: EntitiesListener?,
    val name: String
) : Iterable<Entity> {

    private val entities = Bag<Entity>()

    internal fun add(entity: Entity) {
        entities.add(entity)
        entityAdded?.invoke(entity)
    }

    internal fun isInterested(entityBits: Bits) = (allMask == null || entityBits.contains(allMask)) &&
            (anyMask == null || entityBits.intersects(anyMask)) &&
            (noneMask == null || !entityBits.intersects(noneMask))

    override fun iterator(): Iterator<Entity> = entities.asSequence().filterNotNull().iterator()

    internal fun remove(entity: Entity) {
        entities.remove(entity)
        entityRemoved?.invoke(entity)
    }

    internal fun update(entity: Entity) {
        entityUpdated?.invoke(entity)
    }

    class Builder internal constructor(
        private val componentService: ComponentService,
        private val entityService: EntityService
    ) {
        private var allMask: Bits? = null
        private var anyMask: Bits? = null
        private var entityAdded: EntitiesListener? = null
        private var entityRemoved: EntitiesListener? = null
        private var entityUpdated: EntitiesListener? = null
        private var noneMask: Bits? = null
        private var name: String = ""

        fun build(): Entities {
            val newEntities = Entities(allMask, anyMask, noneMask, entityAdded, entityRemoved, entityUpdated, name)
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

        fun entityAdded(listener: EntitiesListener) {
            entityAdded = listener
        }

        fun entityRemoved(listener: EntitiesListener) {
            entityRemoved = listener
        }

        fun entityUpdated(listener: EntitiesListener) {
            entityUpdated = listener
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