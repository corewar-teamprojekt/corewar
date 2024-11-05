package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Jmp
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestJmp {
    @Test
    fun testExecute() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val settings = InternalSettings(8000, 1000, dat, 1000)
        val shork = InternalShork(settings)
        val program = Program("Jumpy :3", shork)
        val process = Process(program, 0)

        val jmp = Jmp(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)

        jmp.execute(process)
        assert(process.programCounter == 42)
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
