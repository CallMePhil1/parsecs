package parsecs.ecs.component

abstract class Component {
    var inUse: Boolean = false
    abstract fun reset()
}