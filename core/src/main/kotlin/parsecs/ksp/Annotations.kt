package parsecs.ksp

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
annotation class Entities(
    val with: Array<KClass<*>> = []
)
