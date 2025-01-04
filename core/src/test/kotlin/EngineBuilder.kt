import com.github.callmephil1.parsecs.ecs.component.ComponentService
import com.github.callmephil1.parsecs.ecs.engine
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

class TestService

class TestService2(private val testService: TestService)

class EngineBuilderTests {
    @Test
    fun `Create an engine builder and add systems it should build successfully`() {
        val engine = engine {
            systems {
                add(TestRenderSystem::class.java)
                add(TestUpdateSystem::class.java)
            }
        }
    }

    @Test
    fun `Create an engine builder and add services it should build successfully`() {
        val engine = engine {
            services {
                singleton(TestService::class.java)
                singleton(TestService2::class.java)
            }
        }
    }
}