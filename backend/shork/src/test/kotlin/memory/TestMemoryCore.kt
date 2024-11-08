package memory

import getDefaultInternalSettings
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

internal class TestMemoryCore {
    val defaultInstruction =
        MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
    val settings =
        getDefaultInternalSettings(defaultInstruction, gameDataCollector = MockGameDataCollector())

    var memoryCore = MemoryCore(8000, settings)

    @BeforeEach
    fun setup() {
        memoryCore = MemoryCore(8000, settings)
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
        val memoryCore = MemoryCore(10, settings)

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(20, instruction)
        val out = memoryCore.loadAbsolute(20)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun testReadWriteOutOfBoundsNegative() {
        val memoryCore = MemoryCore(10, settings)

        val instruction = MockInstruction(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        memoryCore.storeAbsolute(-13, instruction)
        val out = memoryCore.loadAbsolute(7)

        assertNotEquals(defaultInstruction, out)
        assertEquals(instruction, out)
    }

    @Test
    fun `test if the memory core integrates with the Game Data Collector`() {
        val settings = getDefaultInternalSettings(defaultInstruction)
        val shork = InternalShork(settings)
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

    @Test
    fun testResolveForReadBoundsPositive() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore10 =
            MemoryCore(100, getDefaultInternalSettings(defaultInstruction, readDistance = 10))

        val out = memoryCore10.resolveForReading(42, 23, AddressMode.DIRECT)
        assertEquals(45, out)

        val out2 = memoryCore10.resolveForReading(42, 34, AddressMode.DIRECT)
        assertEquals(46, out2)

        val memoryCore32 =
            MemoryCore(256, getDefaultInternalSettings(defaultInstruction, readDistance = 32))

        for (i in 0 until 100) {
            for (j in 0 until 32) {
                val out = memoryCore32.resolveForReading(i, j, AddressMode.DIRECT)
                assertEquals(i + j % 32, out)
            }
        }
    }

    @Test
    fun testResolveForReadBoundsNegative() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore10 =
            MemoryCore(100, getDefaultInternalSettings(defaultInstruction, readDistance = 10))

        val out = memoryCore10.resolveForReading(42, -23, AddressMode.DIRECT)
        assertEquals(39, out)

        val out2 = memoryCore10.resolveForReading(42, -34, AddressMode.DIRECT)
        assertEquals(38, out2)

        val memoryCore32 =
            MemoryCore(256, getDefaultInternalSettings(defaultInstruction, readDistance = 32))

        for (i in 0 until 100) {
            for (j in 0 until 32) {
                val out = memoryCore32.resolveForReading(i, -j, AddressMode.DIRECT)
                assertEquals(i - j % 32, out)
            }
        }
    }

    @Test
    fun testResolveForWriteBoundsPositive() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore10 =
            MemoryCore(100, getDefaultInternalSettings(defaultInstruction, writeDistance = 10))

        val out = memoryCore10.resolveForWriting(42, 23, AddressMode.DIRECT)
        assertEquals(45, out)

        val out2 = memoryCore10.resolveForWriting(42, 34, AddressMode.DIRECT)
        assertEquals(46, out2)

        val memoryCore32 =
            MemoryCore(256, getDefaultInternalSettings(defaultInstruction, writeDistance = 32))

        for (i in 0 until 100) {
            for (j in 0 until 32) {
                val out = memoryCore32.resolveForWriting(i, j, AddressMode.DIRECT)
                assertEquals(i + j % 32, out)
            }
        }
    }

    @Test
    fun testResolveForWriteBoundsNegative() {
        val defaultInstruction =
            MockInstruction(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val memoryCore10 =
            MemoryCore(100, getDefaultInternalSettings(defaultInstruction, writeDistance = 10))

        val out = memoryCore10.resolveForWriting(42, -23, AddressMode.DIRECT)
        assertEquals(39, out)

        val out2 = memoryCore10.resolveForWriting(42, -34, AddressMode.DIRECT)
        assertEquals(38, out2)

        val memoryCore32 =
            MemoryCore(256, getDefaultInternalSettings(defaultInstruction, writeDistance = 32))

        for (i in 0 until 100) {
            for (j in 0 until 32) {
                val out = memoryCore32.resolveForWriting(i, -j, AddressMode.DIRECT)
                assertEquals(i - j % 32, out)
            }
        }
    }
}
