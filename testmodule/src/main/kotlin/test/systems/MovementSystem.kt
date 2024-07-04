package test.systems

import parsecs.ecs.component.Components
import parsecs.ecs.entity.EntityQuery
import parsecs.ecs.system.UpdateSystem
import test.components.PositionComponent
import test.components.VelocityComponent

internal class MovementSystem : UpdateSystem {
    private val entities = EntityQuery()
        .has(PositionComponent::class, VelocityComponent::class)

    val positionMapper = Components.mapper<PositionComponent>()
    val velocityMapper = Components.mapper<VelocityComponent>()

    override fun update(delta: Float) {
        entities.forEach {
            val position = positionMapper(it)
            val velocityComponent = velocityMapper(it)

            position.x += velocityComponent.x
            position.y += velocityComponent.y
        }
    }
}
