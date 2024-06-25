package parsecs.ksp

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
annotation class SystemEntities(
    val with: Array<KClass<*>> = []
)
