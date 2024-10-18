package instruction

import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat

internal class TestDat {

    // deepCopy test exercises deepCopy and equals
    @Test
    fun testDeepCopy() {
        val dat = Dat(1, 2, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        val copy = dat.deepCopy()

        assert(dat !== copy)
        assert(dat == copy)
    }

    @Test fun testExecute() {}
}
