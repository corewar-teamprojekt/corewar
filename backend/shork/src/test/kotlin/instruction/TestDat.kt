package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestDat {
    @Test
    fun testExecute() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val settings = InternalSettings(8000, 1000, dat, 1000, 100, 64)
        val shork = InternalShork(settings)
        val program = Program("A-", shork)
        val process = Process(program, 0)

        dat.execute(process)
        assert(program.processes.isEmpty())
    }

    @Test
    fun testDeepCopy() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val copy = dat.deepCopy()

        assertEquals(dat.aField, copy.aField)
        assertEquals(dat.bField, copy.bField)
        assertEquals(dat.addressModeA, copy.addressModeA)
        assertEquals(dat.addressModeB, copy.addressModeB)
        assertEquals(dat.modifier, copy.modifier)
        assert(dat !== copy)
    }
}
