package parsecs.interfaces

abstract class Component {
    internal var inUse: Boolean = false
    abstract fun reset()
}