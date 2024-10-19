package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Jump
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestJump {
    @Test
    fun testExecute() {
        val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val settings = InternalSettings(8000, 1000, dat, 1000)
        val shork = InternalShork(settings)
        val program = Program("Jumpy :3", shork)
        val process = Process(program, 0)

        val jump = Jump(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)

        jump.execute(process)
        assert(process.programCounter == 42)
    }

    @Test
    fun testDeepCopy() {
        val jump = Jump(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        val copy = jump.deepCopy()

        assertEquals(jump.aField, copy.aField)
        assertEquals(jump.bField, copy.bField)
        assertEquals(jump.addressModeA, copy.addressModeA)
        assertEquals(jump.addressModeB, copy.addressModeB)
        assertEquals(jump.modifier, copy.modifier)
        assert(jump !== copy)
    }
}
