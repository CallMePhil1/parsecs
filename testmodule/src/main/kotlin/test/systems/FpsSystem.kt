package test.systems

import com.github.callmephil1.parsecs.ecs.system.MonitorSystem

internal class FpsSystem : MonitorSystem {
    override fun draw(delta: Float) {
        //println("Render FPS: ${1 / delta}")
    }

    override fun update(delta: Float) {
        //println("Update FPS: ${1 / delta}")
    }
}