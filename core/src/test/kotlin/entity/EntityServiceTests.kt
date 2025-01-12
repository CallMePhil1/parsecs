package entity

import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import kotlin.test.Test

class EntityServiceTests {
    @Test
    fun `Get new entity should succeed`() {
        val componentService = ComponentService()
        val entityService = EntityService(componentService)

        val entity = entityService.entity {  }
    }
}