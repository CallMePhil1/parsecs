package com.github.callmephil1.parsecs.ksp.entity

import com.github.callmephil1.parsecs.ecs.entity.EntityID

interface Entities {
    fun forEach(block: (EntityID) -> Unit)
}
