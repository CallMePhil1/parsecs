package test

import com.github.callmephil1.parsecs.ecs.Engine
import com.github.callmephil1.parsecs.ecs.component.Components
import com.github.callmephil1.parsecs.ecs.system.Systems
import test.components.PositionComponent
import test.components.VelocityComponent
import test.systems.ForceSystem
import test.systems.FpsSystem
import test.systems.MovementSystem

internal fun main() {
    Components.registerComponent<PositionComponent>()
    Components.registerComponent<VelocityComponent>()

    Systems.addSystem(MovementSystem())
    Systems.addSystem(ForceSystem())
    Systems.addSystem(FpsSystem())

//    Engine.fpsLimit = 30
//    Engine.updatesPerSecond = 60
    Engine.initialize()

    while (true) {
        Engine.update()
    }
}