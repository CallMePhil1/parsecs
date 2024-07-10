package test.systems

import com.github.callmephil1.parsecs.ecs.component.Components
import com.github.callmephil1.parsecs.ecs.entity.Entities
import com.github.callmephil1.parsecs.ecs.entity.EntityQuery
import com.github.callmephil1.parsecs.ecs.system.UpdateSystem
import test.components.LifeSpanComponent
import test.entityCount

class LifeSpanSystem : UpdateSystem {
    val entities = EntityQuery().has(LifeSpanComponent::class)
    val lifeSpanMapper = Components.mapper<LifeSpanComponent>()

    override fun update(delta: Float) {
        entities.forEach {
            val lifeSpanComponent = lifeSpanMapper(it)
            lifeSpanComponent.passedTime += delta

            if (lifeSpanComponent.passedTime >= lifeSpanComponent.deathTime) {
                Entities.releaseEntityID(it)
                entityCount -= 1
            }
        }
    }
}