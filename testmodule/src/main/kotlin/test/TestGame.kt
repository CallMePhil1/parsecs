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
                deathTime = Random.nextFloat() * (5 until 10).random()
            }
        }
        entityCount += 1
    }
    println(entityCount)
}

internal fun main() {
    var stopSpawning = false
    Components.registerComponent(PositionComponent::class)
    Components.registerComponent(VelocityComponent::class)
    Components.registerComponent(LifeSpanComponent::class)

    Systems.addSystem(MovementSystem())
    Systems.addSystem(ForceSystem())
    Systems.addSystem(FpsSystem())
    Systems.addSystem(LifeSpanSystem())

//    Engine.fpsLimit = 30
//    Engine.updatesPerSecond = 60
    Engine.initialize()

    while (true) {
        Engine.update()

        if (!stopSpawning && entityCount < 800_000) {
            addEntity()
        } else {
            stopSpawning = true
        }
    }
}