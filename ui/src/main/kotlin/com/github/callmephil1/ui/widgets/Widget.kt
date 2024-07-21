package com.github.callmephil1.ui.widgets

import com.badlogic.gdx.math.Vector2

interface Widget {
    fun render(availableSize: Vector2, position: Vector2)
}