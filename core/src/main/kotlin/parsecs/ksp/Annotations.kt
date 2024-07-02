package parsecs.ksp

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class EntityQuery(
    val with: Array<KClass<*>> = []
)
