package com.github.callmephil1.parsecs.ecs.component

import com.github.callmephil1.parsecs.ecs.entity.EntityID
import kotlin.reflect.KClass

typealias ArrayResizer = (size: Int) -> Unit
typealias ComponentMapper<T> = (EntityID) -> T

object Components {
    val logger: System.Logger = System.getLogger(Components::class.qualifiedName)

    private val componentArrays = mutableListOf<Array<out Component>>()
    private val componentClasses = mutableListOf<KClass<*>>()
    private val componentResizer = mutableListOf<ArrayResizer>()

    fun <T : Component> addComponent(entity: EntityID, cls: KClass<T>, init: T.() -> Unit) {
        val index = componentClasses.indexOf(cls)
        val component = componentArrays[index][entity] as T
        component.inUse = true
        component.init()
    }

    internal fun getComponentArray(cls: KClass<*>): Array<out Component> {
        val arrayIndex = componentClasses.indexOf(cls)

        if (arrayIndex < 0)
            logger.log(System.Logger.Level.ERROR) { "Could not find array for component '${cls.qualifiedName}'" }

        return componentArrays[arrayIndex]
    }

    internal fun hardCompact(count: Int) {
        componentArrays.forEach {

        }
    }

    fun <T: Component> mapper(cls: KClass<T>): ComponentMapper<T> {
        val index = componentClasses.indexOf(cls)

        if (index == -1)
            throw IndexOutOfBoundsException("Component '${cls.qualifiedName}' was not found when creating mapper")

        return {
            componentArrays[index][it] as T
        }
    }

    fun <T: Component> registerComponent(cls: KClass<T>) {
        val ctor = cls.java.constructors.firstOrNull { it.parameterCount == 0 }

        if (ctor == null) {
            logger.log(System.Logger.Level.ERROR) { "Component '${cls.qualifiedName}' does not contain an empty constructor" }
            return
        }

        val test = ctor.let {
            if (!componentClasses.contains(cls)) {

                val initArray = Array(1) { ctor.newInstance() as Component }
                val index = componentClasses.size
                componentClasses.add(cls)
                componentArrays.add(initArray)
                componentResizer.add { newSize ->
                    val array = componentArrays[index] as Array<T>
                    val newArray = Array(newSize) {
                        if (it < array.size) array[it]
                        else ctor.newInstance() as Component
                    }
                    componentArrays[index] = newArray
                }
            }
            return@let 1
        }
    }

    internal fun resize(newSize: Int) {
        logger.log(System.Logger.Level.DEBUG) { "Resizing component arrays to a size of $newSize" }
        for (i in componentClasses.indices) {
            componentResizer[i].invoke(newSize)
        }
    }
}