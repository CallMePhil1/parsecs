package com.github.callmephil1.parsecs.ecs.collections

import java.util.Stack

class Pool<T>(
    clazz: Class<T>,
    private val capacity: Int = Int.MAX_VALUE
) {
    private val ctor = clazz.getDeclaredConstructor()
    private val pool: Stack<T> = Stack()

    fun obtain(): T {
        val item: T = if (pool.isEmpty())
            ctor.newInstance()
        else
            pool.pop()
        return item
    }

    fun release(item: T?) {
        if(item == null)
            return

        if (pool.size < capacity)
            pool.push(item)
    }
}
