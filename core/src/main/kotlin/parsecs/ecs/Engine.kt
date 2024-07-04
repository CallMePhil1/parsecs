package parsecs.ecs

import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ecs.system.*
import parsecs.ext.SystemExt.nanoToSeconds
import java.lang.System

object Engine {
    private val logger = KotlinLogging.logger {}

    private var deltaRenderTime: Float = 0f
    private var deltaUpdateTime: Float = 0f
    private var previousNanoSeconds: Long = 0
    private var targetRenderTime: Float = 0f
    private var targetUpdateTime: Float = 0f

    var fpsLimit: Int = 0
        set(value) {
            targetRenderTime = if (value == 0) 0f else 1f / value
            field = value
        }

    var updatesPerSecond: Int = 0
        set(value) {
            targetUpdateTime = if (value == 0) 0f else 1f / value
            field = value
        }

    fun initialize() {
        logger.info { "Initializing engine" }
    }

    fun update() {
        Systems.engineLoopStart()

        if (previousNanoSeconds == 0L)
            previousNanoSeconds = System.nanoTime()

        val currentNanoSeconds = System.nanoTime()
        val timeDiff = currentNanoSeconds - previousNanoSeconds
        val delta = timeDiff.nanoToSeconds()

        deltaRenderTime += delta
        deltaUpdateTime += delta

        if (deltaUpdateTime >= targetUpdateTime) {
            Systems.updateSystemLoop(deltaUpdateTime)
        }

        if (deltaRenderTime >= targetRenderTime) {
            Systems.renderSystemLoop(deltaRenderTime)
        }

        Systems.monitorSystemUpdateLoop(deltaUpdateTime)
        Systems.monitorSystemRenderLoop(deltaRenderTime)

        Systems.engineLoopEnd()

        if (deltaUpdateTime >= targetUpdateTime)
            deltaUpdateTime = 0f

        if (deltaRenderTime >= targetRenderTime)
            deltaRenderTime = 0f

        previousNanoSeconds = currentNanoSeconds
    }
}