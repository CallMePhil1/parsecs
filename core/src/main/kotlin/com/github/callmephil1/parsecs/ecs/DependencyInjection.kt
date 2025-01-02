package com.github.callmephil1.parsecs.ecs

class MissingDependencyError(
    clazz: Class<*>,
    paramClass: Class<*>
) : Error("Missing dependency '${paramClass.name}' for '${clazz.name}'")

class DependencyInjectionBuilder {
    private val getters = mutableMapOf<Class<*>, (DependencyInjection) -> Any>()
    private val singletons = mutableMapOf<Class<*>, Any>()

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

    fun <T : Any> scoped(clazz: Class<T>, factory: DependencyInjection.() -> T) {
        getters[clazz] = factory
    }

    fun <T : Any> singleton(clazz: Class<T>): DependencyInjectionBuilder {
        val ctor = clazz.constructors[0]
        val params = ctor.parameterTypes

        val clazzGetter: DependencyInjection.() -> Any = getter@{
            if (singletons.containsKey(clazz))
                return@getter singletons[clazz]!!

            val paramInstances = params.map { this.get(it) }.toTypedArray()

            val service = ctor.newInstance(*paramInstances)!!
            singletons[clazz] = service
            return@getter service
        }

        getters[clazz] = clazzGetter
        return this
    }

    fun <T : Any> singleton(clazz: Class<T>, factory: DependencyInjection.() -> T): DependencyInjectionBuilder {
        val singletonGetter: DependencyInjection.() -> T = getter@{
            if (singletons.containsKey(clazz))
                return@getter singletons[clazz]!! as T

            val service = this.factory()
            singletons[clazz] = service
            return@getter service
        }

        getters[clazz] = singletonGetter
        return this
    }
}

class DependencyInjection internal constructor(
    private val getters: MutableMap<Class<*>, DependencyInjection.() -> Any>
) {
    fun <T> get(clazz: Class<T>) = getters[clazz]!!.invoke(this) as T
}