package test

import parsecs.ecs.system.UpdateSystem
import parsecs.entity.Entities
import parsecs.ksp.SystemEntities

@SystemEntities(
    with = [TestComponent::class, AnotherComponent::class]
)
class TestSystems : UpdateSystem {
    override fun update(delta: Float) {
        TestSystemsEntities.forEach {

        }
    }
}

@SystemEntities(
    with = [TestComponent::class, AnotherComponent::class, OtherComponent::class]
)
class OtherSystems : UpdateSystem {
    override fun update(delta: Float) {
        OtherSystemsEntities.forEach {
        }
    }
}
