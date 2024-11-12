package instruction

import kotlin.test.assertEquals
import mocks.MockGameDataCollector
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Djn
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestDjn {

    private val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    private val settings =
        InternalSettings(8000, 1000, dat, 1000, 100, gameDataCollector = MockGameDataCollector())
    private var shork = InternalShork(settings)
    private var program = Program("djn", shork)

    @BeforeEach
    fun setup() {
        shork = InternalShork(settings)
        program = Program("djn", shork)
        program.createProcessAt(0)
    }

    @Test
    fun `test jump unsuccessful if value is zero after decrement with modifier a`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        val dat2 = Dat(1, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(0, dat2.aField)
        assertEquals(42, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if value is zero after decrement with modifier ba`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        val dat2 = Dat(1, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(0, dat2.aField)
        assertEquals(42, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if value is zero after decrement with modifier b`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        val dat2 = Dat(42, 1, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(42, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if value is zero after decrement with modifier ab`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        val dat2 = Dat(42, 1, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(42, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if only one value is non-zero after decrement with modifier f`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        val dat2 = Dat(2, 1, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(1, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if only one value is non-zero with modifier x`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        val dat2 = Dat(2, 1, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(1, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump unsuccessful if only one value is non-zero with modifier i`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val dat2 = Dat(2, 1, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(1, program.processes.get().programCounter)
        assertEquals(1, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump successful if value is non-zero after decrement with modifier a`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        val dat2 = Dat(42, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(41, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump successful if value is non-zero after decrement with modifier ba`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        val dat2 = Dat(42, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(41, dat2.aField)
        assertEquals(0, dat2.bField)
    }

    @Test
    fun `test jump successful if value is non-zero after decrement with modifier b`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        val dat2 = Dat(0, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(0, dat2.aField)
        assertEquals(41, dat2.bField)
    }

    @Test
    fun `test jump successful if value is non-zero after decrement with modifier ab`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        val dat2 = Dat(0, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(0, dat2.aField)
        assertEquals(41, dat2.bField)
    }

    @Test
    fun `test jump successful if both values are non-zero after decrement with modifier f`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        val dat2 = Dat(42, 3, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(41, dat2.aField)
        assertEquals(2, dat2.bField)
    }

    @Test
    fun `test jump successful if both values are non-zero after decrement with modifier i`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        val dat2 = Dat(42, 3, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(41, dat2.aField)
        assertEquals(2, dat2.bField)
    }

    @Test
    fun `test jump successful if both values are non-zero after decrement with modifier x`() {
        val djn = Djn(69, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        val dat2 = Dat(42, 3, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        program.shork.memoryCore.storeAbsolute(0, djn)
        program.shork.memoryCore.storeAbsolute(1, dat2)
        program.tick()

        assertEquals(69, program.processes.get().programCounter)
        assertEquals(41, dat2.aField)
        assertEquals(2, dat2.bField)
    }

    @Test
    fun testDeepCopy() {
        val djn = Djn(42, 0, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        val copy = djn.deepCopy()

        assertEquals(djn.aField, copy.aField)
        assertEquals(djn.bField, copy.bField)
        assertEquals(djn.addressModeA, copy.addressModeA)
        assertEquals(djn.addressModeB, copy.addressModeB)
        assertEquals(djn.modifier, copy.modifier)
        assert(djn !== copy)
    }
}
