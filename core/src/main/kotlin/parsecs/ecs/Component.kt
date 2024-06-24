package parsecs.ecs

abstract class Component {
    var inUse: Boolean = false
    abstract fun reset()
}