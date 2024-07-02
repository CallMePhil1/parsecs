package parsecs.ksp.entity

import parsecs.ecs.entity.EntityID

interface Entities {
    fun forEach(block: (EntityID) -> Unit)
}
