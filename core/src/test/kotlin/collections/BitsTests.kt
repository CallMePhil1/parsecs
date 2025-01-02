package collections

import com.github.callmephil1.parsecs.ecs.collections.Bits
import kotlin.test.Test
import kotlin.test.assertEquals

class BitsTests {
    @Test
    fun `Setting bit for bits`() {
        val bits = Bits()

        bits[4] = true
        bits[6] = true

        assertEquals(bits[6], true)

        bits[4] = false

        assertEquals(bits[4], false)
        assertEquals(bits[6], true)
    }
}