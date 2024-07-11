package com.github.callmephil1.parsecs.ecs.entity

import com.github.callmephil1.parsecs.ecs.component.Component
import com.github.callmephil1.parsecs.ecs.component.Components
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class EntityQuery {
    private val has: MutableList<KClass<*>> = mutableListOf()
    private val notHave: MutableList<KClass<*>> = mutableListOf()

    fun forEach(block: (EntityID) -> Unit) {
        val inUse = Entities.inUse
        val tail = Entities.tail

        val hasArrays = has.map { Components.getComponentArray(it) }
        val notHaveArrays = notHave.map { Components.getComponentArray(it) }

        for (i in 0 .. tail) {
            if (inUse[i]) {
                if (hasArrays.all { it[i].inUse } && notHaveArrays.all { !it[i].inUse }) {
                    block(i)
                }
            }
        }
    }

    fun has(vararg components: KClass<*>) = apply {
        components.forEach {
            if (it.isSubclassOf(Component::class)) {
                logger.log(System.Logger.Level.DEBUG) { "Add component '${it.qualifiedName}' to has list" }
                has.add(it)
            } else {
                logger.log(System.Logger.Level.DEBUG) { "Tried adding component '${it.qualifiedName}' to has list but it does not inherit 'Component'" }
            }
        }
    }

    fun notHave(vararg components: KClass<*>) = apply {
        components.forEach {
            if (it.isSubclassOf(Component::class)) {
                logger.log(System.Logger.Level.DEBUG) { "Add component '${it.qualifiedName}' to not have list" }
                notHave.add(it)
            } else {
                logger.log(System.Logger.Level.DEBUG) { "Tried adding component '${it.qualifiedName}' to not have list but it does not inherit 'Component'" }
            }
        }
    }

    companion object {
        private val logger = System.getLogger(EntityQuery::class.qualifiedName)
    }
}
