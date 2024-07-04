package test

import parsecs.ecs.Engine
import parsecs.ecs.component.Components
import parsecs.ecs.system.Systems
import test.components.PositionComponent
import test.components.VelocityComponent
import test.systems.ForceSystem
import test.systems.FpsSystem
import test.systems.MovementSystem

fun main() {
    Components.registerComponent<PositionComponent>()
    Components.registerComponent<VelocityComponent>()

    Systems.addSystem(MovementSystem())
    Systems.addSystem(ForceSystem())
    Systems.addSystem(FpsSystem())

    Engine.fpsLimit = 30
    Engine.updatesPerSecond = 60
    Engine.initialize()

    while (true) {
        Engine.update()
    }
}