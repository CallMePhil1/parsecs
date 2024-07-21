package com.github.callmephil1.ui.widgets

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

class Column(
    val shapeRenderer: ShapeRenderer,
    val spriteBatch: SpriteBatch,
    init: Column.() -> Unit
) : Widget {

    private val children: MutableList<Widget> = mutableListOf()

    init {
        this.init()
    }

    fun add(widget: Widget) {
        children.add(widget)
    }

    override fun render(availableSize: Vector2, position: Vector2) {
        val childSize = availableSize.mulAdd(Vector2.Zero, 1f / children.size)
        children.forEachIndexed { index, widget ->
            val widgetPosition = Vector2(position.x + index * childSize.x, position.y + index * childSize.y)
            widget.render(childSize, widgetPosition)
        }
    }
}