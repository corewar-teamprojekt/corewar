package program

import assertExecutionCountAtAddress
import kotlin.test.assertEquals
import mocks.MockInstruction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestProgram {
    private var settings = InternalSettings(8000, 100, MockInstruction(), 1000)
    private var shork = InternalShork(settings)
    private var program = Program("id", shork)

    @BeforeEach
    fun beforeEach() {
        shork = InternalShork(settings)
        program = Program("id", shork)
    }

    @Test
    fun testNoProcesses() {
        // Shouldn't blow up and be a no-op
        program.tick()

        // The memory shouldn't be modified
        for (i in 0 until 8000) {
            val instruction = shork.memoryCore.loadAbsolute(i)
            instruction as MockInstruction
            assertEquals(0, instruction.executionCount)
        }
    }

    @Test
    fun testTickSingleProgram() {
        program.createProcessAt(42)

        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)

        // Tick again, it should move forward
        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
    }

    @Test
    fun testTickMultiplePrograms() {
        program.createProcessAt(42)
        program.createProcessAt(420)

        // First process should be ticked
        program.tick()

        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)

        // Now the second process should get ticked
        program.tick()

        // Both are now at 1 execution
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 1)

        // Tick again, 2 processes means the first one should be ticked again
        program.tick()

        // Program 1 was ticked again, it moved forward
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
    }

    // This test tests with multiple processes and removal of a program
    @Test
    fun testRemoveProgram() {
        program.createProcessAt(42)
        program.createProcessAt(420)

        val process1 = program.processes.get()
        val process2 = program.processes.get()

        program.tick()

        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)

        program.removeProcess(process2)

        program.tick()

        // The second process was removed, so it's start instruction shouldn't be executed
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
    }

    @Test
    fun testRemoveProgramComplex() {
        program.createProcessAt(42)
        program.createProcessAt(420)
        program.createProcessAt(4200)

        val process1 = program.processes.get()
        val process2 = program.processes.get()
        val process3 = program.processes.get()

        // First process should be ticket, second and third should not
        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)
        assertExecutionCountAtAddress(shork.memoryCore, 4200, 0)

        // Gonna remove the middle / second process
        program.removeProcess(process2)

        // Third process should now get ticked, second should not
        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)
        assertExecutionCountAtAddress(shork.memoryCore, 4200, 1)

        // Now the first process should get ticked again
        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)
        assertExecutionCountAtAddress(shork.memoryCore, 4200, 1)

        // Now the third process should get ticked again, the second one should still not be ticked
        program.tick()
        assertExecutionCountAtAddress(shork.memoryCore, 42, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 43, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 420, 0)
        assertExecutionCountAtAddress(shork.memoryCore, 4200, 1)
        assertExecutionCountAtAddress(shork.memoryCore, 4201, 1)
    }

    @Test
    fun testIsAlive() {
        // No processes, should be dead 💀💀💀
        assertEquals(false, program.isAlive())

        // Adding a process, should be alive
        program.createProcessAt(42)
        assertEquals(true, program.isAlive())

        // Dead again :(
        program.removeProcess(program.processes.get())
        assertEquals(false, program.isAlive())
    }
}
