package com.github.callmephil1.parsecs.ecs.component

import com.github.callmephil1.parsecs.ecs.entity.Entity

class ComponentService internal constructor() {
    private var mapperCount = 0
    private val componentMappers = mutableListOf<ComponentMapper<*>>()
    private val componentMapperIndices = mutableMapOf<Class<*>, Int>()

    fun <T> getMapper(clazz: Class<T>): ComponentMapper<T> {
        val index = componentMapperIndices[clazz]

        if (index != null)
            return componentMappers[index] as ComponentMapper<T>

        val newMapper = ComponentMapper(
            clazz = clazz,
            index = mapperCount++
        )
        componentMapperIndices[clazz] = newMapper.index
        componentMappers.add(newMapper)
        return newMapper
    }

    internal fun <T> getComponentIndex(clazz: Class<T>) = getMapper(clazz).index

    internal fun <T> hasComponent(entity: Entity, clazz: Class<T>): Boolean {
        val mapper = getMapper(clazz)
        return entity.componentMask.getBit(mapper.index)
    }

    internal fun release(entity: Entity, with: Entity) {
        for (i in componentMappers.indices)
            componentMappers[i].release(entity = entity, with = with)
    }
}