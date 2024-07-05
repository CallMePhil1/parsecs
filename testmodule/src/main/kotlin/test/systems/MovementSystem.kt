package test.systems

import com.github.callmephil1.parsecs.ecs.component.Components
import com.github.callmephil1.parsecs.ecs.entity.EntityQuery
import com.github.callmephil1.parsecs.ecs.system.UpdateSystem
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
