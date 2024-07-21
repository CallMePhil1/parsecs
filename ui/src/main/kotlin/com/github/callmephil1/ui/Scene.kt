package com.github.callmephil1.ui

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.github.callmephil1.ui.widgets.Widget

class Scene(
    var position: Vector2 = Vector2.Zero,
    var size: Vector2,
    private val shapeRenderer: ShapeRenderer,
    private val spriteBatch: SpriteBatch,
    init: Scene.() -> Unit
) {
    init {
        init()
    }

    lateinit var child: Widget

    fun render() {
        child.render(availableSize = size, position = position)
    }
}