package com.github.callmephil1.parsecs.ecs.collections

import java.util.Stack

class Pool<T>(
    private val clazz: Class<T>
) {
    private var disposable = AutoCloseable::class.java.isAssignableFrom(clazz)
    private val pool: Stack<T> = Stack()

    fun obtain(): T {
        val item: T = if (pool.isEmpty())
            clazz.getDeclaredConstructor().newInstance()
        else
            pool.pop()
        return item
    }

    fun release(item: T?) {
        if(item == null)
            return
        if (disposable)
            (item as AutoCloseable).close()
        pool.push(item)
    }
}
