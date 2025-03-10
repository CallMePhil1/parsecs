package com.github.callmephil1.parsecs.ecs

class MissingDependencyError(
    clazz: Class<*>,
    paramClass: Class<*>
) : Error("Missing dependency '${paramClass.name}' for '${clazz.name}'")

class DependencyInjectionBuilder {
    private val getters = mutableMapOf<Class<*>, (DependencyInjection) -> Any>()

    fun build(): DependencyInjection = DependencyInjection(getters)

    fun <T : Any> scoped(clazz: Class<T>): DependencyInjectionBuilder {
        val ctor = clazz.constructors[0]
        val params = ctor.parameterTypes

        val clazzGetter: DependencyInjection.() -> Any = getter@{
            val paramInstances = params.map { this.get(it) }.toTypedArray()
            return@getter ctor.newInstance(*paramInstances)!!
        }

        getters[clazz] = clazzGetter
        return this
    }

    fun <T : Any> scoped(clazz: Class<T>, factory: DependencyInjection.() -> T): DependencyInjectionBuilder {
        getters[clazz] = factory
        return this
    }

    fun <T : Any> singleton(clazz: Class<T>): DependencyInjectionBuilder {
        val ctor = clazz.constructors[0]
        val params = ctor.parameterTypes

        var singleton: T? = null

        val clazzGetter: DependencyInjection.() -> T = getter@{
            if (singleton != null)
                return@getter singleton as T

            val paramInstances = params.map { this.get(it) }.toTypedArray()

            singleton = ctor.newInstance(*paramInstances)!! as T
            return@getter singleton as T
        }

        getters[clazz] = clazzGetter
        return this
    }

    fun <T : Any> singleton(clazz: Class<T>, factory: DependencyInjection.() -> T): DependencyInjectionBuilder {
        var singleton: T? = null

        val singletonGetter: DependencyInjection.() -> T = getter@{
            if (singleton != null)
                return@getter singleton as T

            singleton = this.factory()
            return@getter singleton as T
        }

        getters[clazz] = singletonGetter
        return this
    }
}

inline fun <reified T : Any> DependencyInjectionBuilder.scoped() = scoped(T::class.java)
inline fun <reified T : Any> DependencyInjectionBuilder.scoped(noinline factory: DependencyInjection.() -> T) = scoped(T::class.java, factory)
inline fun <reified T : Any> DependencyInjectionBuilder.singleton() = singleton(T::class.java)
inline fun <reified T : Any> DependencyInjectionBuilder.singleton(noinline factory: DependencyInjection.() -> T) = singleton(T::class.java, factory)

class DependencyInjection internal constructor(
    private val getters: MutableMap<Class<*>, DependencyInjection.() -> Any>
) {
    fun <T> get(clazz: Class<T>) = getters[clazz]!!.invoke(this) as T
}

fun dependencyInjection(configure: DependencyInjectionBuilder.() -> Unit): DependencyInjection {
    val builder = DependencyInjectionBuilder()
    builder.configure()
    return builder.build()
}