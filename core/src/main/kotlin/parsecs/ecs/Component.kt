package parsecs.ecs

abstract class Component {
    internal var inUse: Boolean = false
    abstract fun reset()
}