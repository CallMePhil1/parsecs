package com.github.callmephil1.parsecs.ecs.component

import io.github.oshai.kotlinlogging.KotlinLogging
import com.github.callmephil1.parsecs.ecs.entity.EntityID
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

typealias ArrayResizer = (size: Int) -> Unit
typealias ComponentMapper<T> = (EntityID) -> T

object Components {
    val logger = KotlinLogging.logger {}

    val componentClasses = mutableListOf<KClass<*>>()
    val componentArrays = mutableListOf<Array<out Component>>()
    val componentResizer = mutableListOf<ArrayResizer>()


    internal fun getComponentArray(cls: KClass<*>): Array<out Component> {
        val arrayIndex = componentClasses.indexOf(cls)

        if (arrayIndex < 0)
            logger.error { "Could not find array for component '${cls.qualifiedName}'" }

        return componentArrays[arrayIndex]
    }

    inline fun <reified T: Component> mapper(): ComponentMapper<T> {
        val cls = T::class
        val index = componentClasses.indexOf(cls)

        if (index == -1)
            throw IndexOutOfBoundsException("Component '${cls.qualifiedName}' was not found when creating mapper")

        return {
            componentArrays[index][it] as T
        }
    }

    inline fun <reified T: Component> registerComponent() {
        val cls = T::class
        val ctor = cls.java.constructors.firstOrNull { it.parameterCount == 0 } as Constructor<T>?

        if (ctor == null) {
            logger.error { "Component '${cls.qualifiedName}' does not contain an empty constructor" }
        }

        ctor!!.let {
            if (!componentClasses.contains(cls)) {
                val index = componentClasses.size
                componentClasses.add(cls)
                componentArrays.add(Array(0) { ctor.newInstance() })
                componentResizer.add { newSize ->
                    val array = componentArrays[index] as Array<T>
                    val newArray = Array(newSize) {
                        if (it < array.size) array[it]
                        else ctor.newInstance()
                    }
                    componentArrays[index] = newArray
                }
            }
        }
    }

    internal fun resize(newSize: Int) {
        logger.debug { "Resizing component arrays to a size of $newSize" }
        for (i in componentClasses.indices) {
            componentResizer[i].invoke(newSize)
        }
    }
}