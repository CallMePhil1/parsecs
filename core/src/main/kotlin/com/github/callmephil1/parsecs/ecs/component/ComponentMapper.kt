package com.github.callmephil1.parsecs.ecs.component

import com.github.callmephil1.parsecs.ecs.collections.Bag
import com.github.callmephil1.parsecs.ecs.collections.Pool
import com.github.callmephil1.parsecs.ecs.entity.Entity

abstract class BaseComponentMapper {
    internal open val index: Int = 0

    abstract fun getObject(entity: Entity): Any
    internal abstract fun release(entity: Entity, with: Entity)
}

class ComponentMapper<T> internal constructor(
    val clazz: Class<T>,
    override val index: Int
) : BaseComponentMapper() {

    private val components = Bag<T>()
    private val pool: Pool<T> = Pool(clazz)

    fun get(index: Int) = components[index]!!

    fun get(entity: Entity) = get(entity.index)

    fun getOrNull(entity: Entity) = components[entity.index]

    override fun getObject(entity: Entity) = get(entity) as Any

    fun obtain() = pool.obtain()

    fun release(entity: Int, with: Int) {
        val component = components[entity]
        pool.release(component)
        components.move(with, entity)
    }

    override fun release(entity: Entity, with: Entity) = release(entity.index, with.index)

    fun set(entity: Entity, value: T) {
        components[entity.index] = value
        entity.componentMask.setBit(index, true)
    }
}