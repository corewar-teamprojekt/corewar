package util

import kotlin.test.assertFailsWith
import mocks.MockInstruction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.util.CircularQueue

internal class TestCircularQueue {

    @Test
    fun testAdd() {
        val queue = CircularQueue<Int>()
        queue.add(1)
        queue.add(2)
        queue.add(3)
        assertEquals(3, queue.size())
    }

    @Test
    fun testGet() {
        val queue = CircularQueue<Int>()
        queue.add(1)
        queue.add(2)
        queue.add(3)
        assertEquals(1, queue.get())
        assertEquals(2, queue.get())
        assertEquals(3, queue.get())

        // Test if the queue actually wraps around
        assertEquals(1, queue.get())
        assertEquals(2, queue.get())
        assertEquals(3, queue.get())
    }

    @Test
    fun testRemoval() {
        val queue = CircularQueue<MockInstruction>()
        val first = MockInstruction()
        val second = MockInstruction()
        val third = MockInstruction()

        queue.add(first)
        queue.add(second)
        queue.add(third)

        queue.removeByReference(second)

        assertEquals(2, queue.size())

        assertEquals(first, queue.get())
        assertEquals(third, queue.get())
        assertEquals(first, queue.get())
    }

    @Test
    fun testUnderflow() {
        val queue = CircularQueue<Int>()
        assertFailsWith<NoSuchElementException> { queue.get() }
    }
}
