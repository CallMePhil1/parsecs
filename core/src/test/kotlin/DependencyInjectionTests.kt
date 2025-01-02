import com.github.callmephil1.parsecs.ecs.DependencyInjectionBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ScopedClass

class ScopedClassWithScopedClass(val scoped: ScopedClass)

class ScopedClassWithSingletonClass(val singleton: SingletonClass)

class SingletonClass

class SingletonWithScoped(val scoped: ScopedClass)

class DependencyInjectionTests {

    @Test
    fun `Getting a singleton multiple times returns the same objects`() {
        val builder = DependencyInjectionBuilder()
            .singleton(SingletonClass::class.java)
        val app = builder.build()

        val singleton = app.get(SingletonClass::class.java)
        val singleton2 = app.get(SingletonClass::class.java)

        assertNotNull(singleton)
        assertNotNull(singleton2)
        assertEquals(singleton, singleton2)
    }

    @Test
    fun `Getting a scoped service multiple times returns different objects`() {
        val builder = DependencyInjectionBuilder()
            .scoped(SingletonClass::class.java)
        val app = builder.build()

        val singleton = app.get(SingletonClass::class.java)
        val singleton2 = app.get(SingletonClass::class.java)

        assertNotNull(singleton)
        assertNotNull(singleton2)
        assertNotEquals(singleton, singleton2)
    }

    @Test
    fun `Getting a singleton service with scoped dependency returns same objects`() {
        val app = DependencyInjectionBuilder()
            .singleton(SingletonWithScoped::class.java)
            .scoped(ScopedClass::class.java)
            .build()

        val singleton = app.get(SingletonWithScoped::class.java)
        val singleton2 = app.get(SingletonWithScoped::class.java)

        assertNotNull(singleton)
        assertNotNull(singleton2)
        assertEquals(singleton, singleton2)
        assertEquals(singleton.scoped, singleton2.scoped)
    }

    @Test
    fun `Getting a scoped service with scoped dependency returns different objects`() {
        val app = DependencyInjectionBuilder()
            .scoped(ScopedClassWithScopedClass::class.java)
            .scoped(ScopedClass::class.java)
            .build()

        val scoped = app.get(ScopedClassWithScopedClass::class.java)
        val scoped2 = app.get(ScopedClassWithScopedClass::class.java)

        assertNotNull(scoped)
        assertNotNull(scoped2)
        assertNotEquals(scoped, scoped2)
        assertNotEquals(scoped.scoped, scoped2.scoped)
    }

    @Test
    fun `Getting a scoped service with singleton dependency returns different scope with equal singleton`() {
        val app = DependencyInjectionBuilder()
            .scoped(ScopedClassWithSingletonClass::class.java)
            .singleton(SingletonClass::class.java)
            .build()

        val scoped = app.get(ScopedClassWithSingletonClass::class.java)
        val scoped2 = app.get(ScopedClassWithSingletonClass::class.java)

        assertNotNull(scoped)
        assertNotNull(scoped2)
        assertNotEquals(scoped, scoped2)
        assertEquals(scoped.singleton, scoped2.singleton)
    }
}