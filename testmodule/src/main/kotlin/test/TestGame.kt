package test

import com.github.callmephil1.parsecs.ecs.Engine
import com.github.callmephil1.parsecs.ecs.component.Components
import com.github.callmephil1.parsecs.ecs.entity.Entities
import com.github.callmephil1.parsecs.ecs.system.Systems
import test.components.LifeSpanComponent
import test.components.PositionComponent
import test.components.VelocityComponent
import test.systems.ForceSystem
import test.systems.FpsSystem
import test.systems.LifeSpanSystem
import test.systems.MovementSystem
import kotlin.random.Random

var entityCount = 0

internal fun addEntity() {
    for (i in 0 .. 10000) {
        Entities.getUnusedEntityID().apply {
            Components.addComponent(this, PositionComponent::class) {}
            Components.addComponent(this, VelocityComponent::class) {}
            Components.addComponent(this, LifeSpanComponent::class) {
                deathTime = Random.nextFloat() * 10
            }
        }
        entityCount += 1
    }
    println(entityCount)
}

internal fun main() {
    Components.registerComponent<PositionComponent>()
    Components.registerComponent<VelocityComponent>()
    Components.registerComponent<LifeSpanComponent>()

    Systems.addSystem(MovementSystem())
    Systems.addSystem(ForceSystem())
    Systems.addSystem(FpsSystem())
    Systems.addSystem(LifeSpanSystem())

//    Engine.fpsLimit = 30
//    Engine.updatesPerSecond = 60
    Engine.initialize()

    while (true) {
        Engine.update()

        if (entityCount % 1000 < 50) {
            println("Another 1000")
        }

        if (entityCount < 1_000_000_000)
            addEntity()
    }
}