package com.github.callmephil1.parsecs.ecs.system

class SystemBuilder {
    private val systems: MutableSet<Class<*>> = mutableSetOf()

    fun <T : System> add(system: Class<T>) {
        systems.add(system)
    }

    fun build(): MutableSet<Class<*>> {
        return systems
    }
}

inline fun <reified T : System> SystemBuilder.add() {
    this.add(T::class.java)
}