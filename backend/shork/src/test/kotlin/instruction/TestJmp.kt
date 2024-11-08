package instruction

import getDefaultInternalSettings
import kotlin.test.assertEquals
import mocks.MockGameDataCollector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Jmp
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program

internal class TestJmp {
    private val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    private var settings =
        getDefaultInternalSettings(dat, gameDataCollector = MockGameDataCollector())
    private var shork = InternalShork(settings)

    @BeforeEach
    fun setUp() {
        shork = InternalShork(settings)
    }

    @Test
    fun testExecute() {
        val program = Program("Jumpy :3", shork)
        val process = Process(program, 0)

        val jmp = Jmp(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)

        jmp.execute(process)
        assert(process.programCounter == 42)
    }

    @Test
    fun `test in conjunction with process`() {
        val core = shork.memoryCore
        val program = Program("Jumpy :3", shork)
        program.createProcessAt(0)
        val jmp = Jmp(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)

        core.storeAbsolute(0, jmp)

        program.tick()

        assertEquals(42, program.processes.get().programCounter)
    }

    @Test
    fun testDeepCopy() {
        val jmp = Jmp(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        val copy = jmp.deepCopy()

        assertEquals(jmp.aField, copy.aField)
        assertEquals(jmp.bField, copy.bField)
        assertEquals(jmp.addressModeA, copy.addressModeA)
        assertEquals(jmp.addressModeB, copy.addressModeB)
        assertEquals(jmp.modifier, copy.modifier)
        assert(jmp !== copy)
    }
}
