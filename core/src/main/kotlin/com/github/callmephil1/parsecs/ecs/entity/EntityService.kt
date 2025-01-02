package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.collections.Bag
import com.github.callmephil1.parsecs.ecs.collections.Pool
import com.github.callmephil1.parsecs.ecs.component.ComponentService
import java.util.*

class EntityService internal constructor(
    private val componentService: ComponentService
) {

    private val createdEntities = Stack<Entity>()
    private val entities  = mutableListOf<Entities>()
    private val releasedEntities = Stack<Entity>()

    private val entityBag = Bag<Entity>()
    private val pool = Pool(Entity::class.java)

    internal fun addEntities(entities: Entities) = this.entities.add(entities)

    internal fun createEntities() {
        while(!createdEntities.empty()) {
            val entity = createdEntities.pop()
            entity.isActive = true

            for(i in entities.indices) {
                if (entities[i].isInterested(entity.componentMask))
                    entities[i].add(entity)
            }
        }
    }

    fun newEntity(): Entity {
        val newEntity = obtain()
        createdEntities.add(newEntity)
        return newEntity
    }

    fun newEntitiesBuilder() = EntitiesBuilder(componentService = componentService, entityService = this)

    internal fun obtain(): Entity {
        val entity = pool.obtain()
        entity.index = entityBag.count
        entityBag.add(entity)
        return entity
    }

    internal fun releaseEntities() {
        while(!releasedEntities.empty()) {
            val entity = releasedEntities.pop()

            for(i in entities.indices) {
                if (entities[i].isInterested(entity.componentMask))
                    entities[i].remove(entity)
            }

            val movedEntity = entityBag.removeAt(entity.index)
            if (movedEntity != null) {
                componentService.release(entity, movedEntity)
                movedEntity.index = entity.index
            }

            entity.index = -1
            entity.componentMask.setAll(false)
            entity.isActive = false
            pool.release(entity)
        }
    }

    fun release(entity: Entity) = releasedEntities.push(entity)
}