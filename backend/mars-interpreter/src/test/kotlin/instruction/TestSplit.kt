package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Split
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestSplit {
    @Test
    fun testExecute() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val settings = InternalSettings(8000, 1000, dat, 1000)
        val shork = InternalShork(settings)
        val program = Program("Splitty", shork)
        val process = Process(program, 0)

        val split = Split(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        split.execute(process)

        assertEquals(program.processes.get().programCounter, 42)
        assert(shork.memoryCore.loadAbsolute(42) is Dat)
    }

    @Test
    fun testDeepCopy() {
        val split = Split(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val copy = split.deepCopy()

        assertEquals(split.aField, copy.aField)
        assertEquals(split.bField, copy.bField)
        assertEquals(split.addressModeA, copy.addressModeA)
        assertEquals(split.addressModeB, copy.addressModeB)
        assertEquals(split.modifier, copy.modifier)
        assert(split !== copy)
    }
}
