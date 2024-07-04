package test.systems

import parsecs.ecs.component.Components
import parsecs.ecs.entity.EntityQuery
import parsecs.ecs.system.UpdateSystem
import test.components.VelocityComponent
import kotlin.random.Random

internal class ForceSystem : UpdateSystem {
    val entities = EntityQuery()
        .has(VelocityComponent::class)

    val velocityMapper = Components.mapper<VelocityComponent>()

    override fun update(delta: Float) {
        entities.forEach {
            val velocityComponent = velocityMapper(it)

            velocityComponent.x += Random.nextFloat() * delta
            velocityComponent.y += Random.nextFloat() * delta
        }
    }
}