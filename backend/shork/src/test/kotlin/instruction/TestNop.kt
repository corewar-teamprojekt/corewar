package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Nop
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestNop {

    private val dat = Dat(5, 13, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    private val settings = InternalSettings(8000, 1000, dat, 1000)
    private var shork = InternalShork(settings)
    private var program = Program("nop", shork)
    private var process = Process(program, 0)

    @BeforeEach
    fun setup() {
        shork = InternalShork(settings)
        program = Program("nop", shork)
        process = Process(program, 0)
    }

    @Test
    fun `test executing with every modifier`() {
        for (mod in Modifier.entries) {
            val nop = Nop(0, 0, AddressMode.DIRECT, AddressMode.DIRECT, mod)
            nop.execute(process)
            assertEquals(0, process.programCounter)
        }
    }

    @Test
    fun testDeepCopy() {
        val nop = Nop(0, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        val copy = nop.deepCopy()

        // Check that the fields are identical
        assertEquals(nop.aField, copy.aField)
        assertEquals(nop.bField, copy.bField)
        assertEquals(nop.addressModeA, copy.addressModeA)
        assertEquals(nop.addressModeB, copy.addressModeB)
        assertEquals(nop.modifier, copy.modifier)

        // Verify that the two instances are not the same object
        assert(nop !== copy)
    }
}
