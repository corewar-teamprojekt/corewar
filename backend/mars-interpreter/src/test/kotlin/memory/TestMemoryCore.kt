package memory

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import mocks.MockInstruction
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.MemoryCore

class TestMemoryCore {
    @Test
    fun testInitializesCorrectly() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore = MemoryCore(8000, defaultInstruction = defaultInstruction)

        for (i in 0 until 8000) {
            val instruction = memoryCore.loadAbsolute(i)
            assert(instruction !== defaultInstruction)
        }
    }

    @Test
    fun testReadWriteInBounds() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore = MemoryCore(8000, defaultInstruction = defaultInstruction)

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(42, instruction)
        val out = memoryCore.loadAbsolute(42)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun testReadWriteOutOfBoundsPositive() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore = MemoryCore(10, defaultInstruction = defaultInstruction)

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(20, instruction)
        val out = memoryCore.loadAbsolute(20)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun testReadWriteOutOfBoundsNegative() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore = MemoryCore(10, defaultInstruction = defaultInstruction)

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(-20, instruction)
        val out = memoryCore.loadAbsolute(20)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }
}
