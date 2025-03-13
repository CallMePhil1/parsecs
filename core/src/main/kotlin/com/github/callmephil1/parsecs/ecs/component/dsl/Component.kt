package com.github.callmephil1.parsecs.ecs.component.dsl

abstract class Component<T> {
    abstract val clazz: Class<T>
    internal var index: Int = -1
}
