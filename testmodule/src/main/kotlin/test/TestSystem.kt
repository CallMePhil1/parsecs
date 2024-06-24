package test

import parsecs.ecs.system.System
import parsecs.ksp.Entities

@Entities(
    with = [TestComponents::class]
)
class TestSystems : System {
}