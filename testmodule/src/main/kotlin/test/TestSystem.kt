package test

import parsecs.ecs.system.UpdateSystem
import parsecs.ksp.Entities

@Entities(
    with = [TestComponent::class, AnotherComponent::class]
)
class TestSystems : UpdateSystem {
    override fun update(delta: Float) {
        TestSystemsEntities.forEach {

        }
    }
}

@Entities(
    with = [TestComponent::class, AnotherComponent::class, OtherComponent::class]
)
class OtherSystems : UpdateSystem {
    override fun update(delta: Float) {
        OtherSystemsEntities.forEach {

        }
    }
}
