package test.components

import com.github.callmephil1.parsecs.ecs.component.Component

class LifeSpanComponent : Component() {
    var deathTime = 0f
    var passedTime = 0f

    override fun reset() {

    }
}