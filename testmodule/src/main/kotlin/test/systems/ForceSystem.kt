package test.systems

import com.github.callmephil1.parsecs.ecs.component.Components
import com.github.callmephil1.parsecs.ecs.entity.EntityQuery
import com.github.callmephil1.parsecs.ecs.system.UpdateSystem
import test.components.VelocityComponent
import kotlin.random.Random

internal class ForceSystem : UpdateSystem {
    private val entities = EntityQuery()
        .has(VelocityComponent::class)

    val velocityMapper = Components.mapper(VelocityComponent::class)

    override fun update(delta: Float) {
        entities.forEach {
            val velocityComponent = velocityMapper(it)

            velocityComponent.x += Random.nextFloat() * delta
            velocityComponent.y += Random.nextFloat() * delta
        }
    }
}