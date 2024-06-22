package parsecs

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
annotation class Entities(vararg val with: KClass<*>)
