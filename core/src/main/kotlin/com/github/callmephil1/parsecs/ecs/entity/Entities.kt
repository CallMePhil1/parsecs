package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.collections.Bag
import com.github.callmephil1.parsecs.ecs.collections.Bits
import com.github.callmephil1.parsecs.ecs.component.ComponentService

class EntitiesBuilder internal constructor(
    private val componentService: ComponentService,
    private val entityService: EntityService
) {
    private val componentMask = Bits()
    private var name: String = ""

    fun build(): Entities {
        val newEntities = Entities(componentMask, name)
        entityService.addEntities(newEntities)
        return newEntities
    }

    fun has(vararg components: Class<*>): EntitiesBuilder {
        for (i in components.indices) {
            val index = componentService.getComponentIndex(components[i])
            componentMask.setBit(index, true)
        }
        return this
    }

    fun name(name: String) {
        this.name = name
    }
}

class Entities internal constructor(
    internal val componentMask: Bits,
    val name: String
) : Iterable<Entity> {

    private val entities = Bag<Entity>()

    internal fun add(entity: Entity) = entities.add(entity)

    internal fun isInterested(entityBits: Bits) = entityBits.maskMatch(componentMask)

    internal fun remove(entity: Entity) = entities.remove(entity)

    override fun iterator(): Iterator<Entity> = entities.asSequence().filterNotNull().iterator()
}