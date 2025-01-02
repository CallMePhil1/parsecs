package component

import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import kotlin.test.Test

class ComponentTest{

}

class ComponentServiceTests {
    @Test
    fun `Get mapper should succeed`() {
        val componentService = ComponentService()
        val entityService = EntityService(componentService)

        val entity = entityService.newEntity()
        val mapper = componentService.getMapper(ComponentTest::class.java)
        mapper.set(entity, ComponentTest())
    }
}