package process

import assertExecutionCountAtAddress
import kotlin.test.assertEquals
import mocks.MockInstruction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestProcess {
    private var settings = InternalSettings(8000, 100, MockInstruction(), 1000)
    private var shork = InternalShork(settings)
    private var program = Program("id", shork)

    @BeforeEach
    fun beforeEach() {
        shork = InternalShork(settings)
        program = Program("id", shork)
    }

    @Test
    fun testTickSingle() {
        program.createProcessAt(42)

        val process = program.processes.get()
        process.tick()

        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
    }

    // Tests multiple ticks on the same process
    @Test
    fun testTickMultiple() {
        program.createProcessAt(42)

        val process = program.processes.get()
        process.tick()
        process.tick()

        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
    }

    // Tests if dontIncrementProgramCounter is working
    @Test
    fun testDontIncrementProgramCounter() {
        program.createProcessAt(42)

        val process = program.processes.get()
        process.dontIncrementProgramCounter = true
        process.tick()

        assertEquals(42, process.programCounter)
    }
}
