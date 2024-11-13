package memory

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import mocks.MockGameDataCollector
import mocks.MockInstruction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.MemoryCore
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestMemoryCore {
    val defaultInstruction =
        MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
    var memoryCore =
        MemoryCore(8000, defaultInstruction = defaultInstruction, MockGameDataCollector())

    @BeforeEach
    fun setup() {
        memoryCore =
            MemoryCore(8000, defaultInstruction = defaultInstruction, MockGameDataCollector())
    }

    @Test
    fun testInitializesCorrectly() {
        for (i in 0 until 8000) {
            val instruction = memoryCore.loadAbsolute(i)
            assert(instruction !== defaultInstruction)
        }
    }

    @Test
    fun testReadWriteInBounds() {
        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(42, instruction)
        val out = memoryCore.loadAbsolute(42)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun testReadWriteOutOfBoundsPositive() {
        val memoryCore =
            MemoryCore(10, defaultInstruction = defaultInstruction, MockGameDataCollector())

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(20, instruction)
        val out = memoryCore.loadAbsolute(20)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun testReadWriteOutOfBoundsNegative() {
        val memoryCore =
            MemoryCore(10, defaultInstruction = defaultInstruction, MockGameDataCollector())

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(-20, instruction)
        val out = memoryCore.loadAbsolute(20)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun `test if the memory core integrates with the Game Data Collector`() {
        val shork = InternalShork(InternalSettings(8000, 1000, defaultInstruction, 1000, 100))
        val memoryCore = shork.memoryCore
        val gameDataCollector = shork.gameDataCollector
        val program = Program("Test", shork)
        program.createProcessAt(0)
        val process = program.processes.first()

        gameDataCollector.startRoundForProgram(program)
        gameDataCollector.collectProcessDataBeforeTick(process)

        memoryCore.loadAbsolute(42)
        memoryCore.loadAbsolute(0)
        memoryCore.loadAbsolute(4200)

        memoryCore.storeAbsolute(42, defaultInstruction)
        memoryCore.storeAbsolute(1337, defaultInstruction)
        memoryCore.storeAbsolute(666, defaultInstruction)

        gameDataCollector.collectProcessDataAfterTick(process)
        gameDataCollector.endRoundForProgram(program)

        val reads = gameDataCollector.getGameStatistics().map { it.memoryReads }.flatten()
        assertEquals(listOf(42, 0, 4200), reads)

        val writes = gameDataCollector.getGameStatistics().map { it.memoryWrites }.flatten()
        assertEquals(listOf(42, 1337, 666), writes)
    }
}
