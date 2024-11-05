package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Spl
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestSpl {
    @Test
    fun testExecute() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val settings = InternalSettings(8000, 1000, dat, 1000, 100)
        val shork = InternalShork(settings)
        val program = Program("Splitty", shork)
        val process = Process(program, 0)

        val spl = Spl(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        spl.execute(process)

        assertEquals(program.processes.get().programCounter, 42)
        assert(shork.memoryCore.loadAbsolute(42) is Dat)
    }

    @Test
    fun testDeepCopy() {
        val spl = Spl(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val copy = spl.deepCopy()

        assertEquals(spl.aField, copy.aField)
        assertEquals(spl.bField, copy.bField)
        assertEquals(spl.addressModeA, copy.addressModeA)
        assertEquals(spl.addressModeB, copy.addressModeB)
        assertEquals(spl.modifier, copy.modifier)
        assert(spl !== copy)
    }
}
