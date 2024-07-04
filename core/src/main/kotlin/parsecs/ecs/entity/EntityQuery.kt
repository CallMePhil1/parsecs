package parsecs.ecs.entity

import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ecs.component.Component
import parsecs.ecs.component.Components
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class EntityQuery {
    private val has: MutableList<KClass<*>> = mutableListOf()
    private val notHave: MutableList<KClass<*>> = mutableListOf()

    fun forEach(block: (EntityID) -> Unit) {
        val inUse = Entities.inUse

        val hasArrays = has.map { Components.getComponentArray(it) }
        val notHaveArrays = notHave.map { Components.getComponentArray(it) }

        for (i in inUse.indices) {
            if (inUse[i]) {
                if (hasArrays.all { it[i].inUse } && notHaveArrays.all { !it[i].inUse }) {
                    block(i)
                }
            }
        }
    }

    fun has(vararg components: KClass<*>) {
        components.forEach {
            if (it.isSubclassOf(Component::class)) {
                logger.debug { "Add component '${it.qualifiedName}' to has list" }
                has.add(it)
            } else {
                logger.debug { "Tried adding component '${it.qualifiedName}' to has list but it does not inherit 'Component'" }
            }
        }
    }

    fun notHave(vararg components: KClass<*>) {
        components.forEach {
            if (it.isSubclassOf(Component::class)) {
                logger.debug { "Add component '${it.qualifiedName}' to not have list" }
                notHave.add(it)
            } else {
                logger.debug { "Tried adding component '${it.qualifiedName}' to not have list but it does not inherit 'Component'" }
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}
