package test

import parsecs.ecs.system.UpdateSystem
import parsecs.ksp.EntityQuery


class TestSystems : UpdateSystem {

    @EntityQuery(
        with = [TestComponent::class, AnotherComponent::class]
    )
    var testQuery: Int = 0

    @EntityQuery(
        with = [TestComponent::class]
    )
    var anotherQuery: Int = 0

    override fun update(delta: Float) {
    }
}

class OtherSystems : UpdateSystem {
    override fun update(delta: Float) {
    }
}
