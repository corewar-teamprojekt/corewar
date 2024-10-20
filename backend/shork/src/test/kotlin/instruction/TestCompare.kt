package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Compare
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Split
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestCompare {
    private val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    private val settings = InternalSettings(8000, 1000, dat, 1000)
    private var shork = InternalShork(settings)
    private var program = Program("Compy", shork)
    private var process = Process(program, 0)

    @BeforeEach
    fun setUp() {
        shork = InternalShork(settings)
        program = Program("Compy", shork)
        process = Process(program, 0)
    }

    @Test
    fun testExecuteModifierAEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierANotEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(43524525, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierBEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierBNotEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(2144213, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierABEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(234134, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierABNotEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(234134, 324124, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierBAEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(1337, 42213, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierBANotEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(123, 42213, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierFAllEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierFAFieldsEqualBFieldsDont() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 21341234, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierFBFieldsEqualAFieldsDont() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(41234, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierXAllEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(1337, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierXAFieldEqualsBFieldNotEqualForFirst() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(1337, 324, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierXAFieldEqualsBFieldNotEqualForSecond() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(324143, 42, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIAllEqual() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(2, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentInstructions() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Split(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentModifier() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.X),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentAddressModesA() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(42, 1337, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentAFieldValues() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(4352, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentAddressModeB() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.A_POST_INCREMENT, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }

    @Test
    fun testExecuteModifierIDifferentBFieldValues() {
        val compare = Compare(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, compare)
        shork.memoryCore.storeAbsolute(
            1,
            Dat(42, 1337, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        shork.memoryCore.storeAbsolute(
            2,
            Dat(42, 141234, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I),
        )
        process.tick()

        assertEquals(1, process.programCounter)
    }
}
