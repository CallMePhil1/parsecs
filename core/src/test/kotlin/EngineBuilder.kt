import com.github.callmephil1.parsecs.ecs.EngineBuilder
import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.entity.EntityService
import com.github.callmephil1.parsecs.ecs.system.RenderSystem
import com.github.callmephil1.parsecs.ecs.system.UpdateSystem
import kotlin.test.Test

class TestComponent {
    var x: Int = 0
}

class TestRenderSystem(
    val componentService: ComponentService,
    val entityService: EntityService
) : RenderSystem {
    override fun draw(delta: Float) {

    }
}

class TestUpdateSystem(
    val componentService: ComponentService,
    val entityService: EntityService
) : UpdateSystem {

    val entities = entityService.newEntitiesBuilder()
        .has(TestComponent::class.java)
        .build()

    override fun update(delta: Float) {

    }
}

class EngineBuilderTests {
    @Test
    fun `Create an engine builder and add systems it should build successfully`() {
        val engine = EngineBuilder()
            .addSystem(TestRenderSystem::class.java)
            .addSystem(TestUpdateSystem::class.java)
            .build()
    }
}