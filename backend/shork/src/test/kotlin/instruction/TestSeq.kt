package instruction

import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Seq
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestSeq {
    private val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    private val settings = InternalSettings(8000, 1000, dat, 1000)
    private var shork = InternalShork(settings)
    private var program = Program("seq", shork)
    private var process = Process(program, 0)

    @BeforeEach
    fun setup() {
        shork = InternalShork(settings)
        program = Program("seq", shork)
        process = Process(program, 0)
    }

    private fun executeSeqAndAssertCounter(seq: Seq, expectedCounterIncrement: Int) {
        seq.execute(process)
        assertEquals(expectedCounterIncrement, process.programCounter)
    }

    private fun setupSeq(modifier: Modifier): Seq {
        val seq = Seq(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, modifier)
        shork.memoryCore.storeAbsolute(0, seq)
        return seq
    }

    private fun setupMemory(aField: Int, bField: Int, address: Int) {
        val dat = Dat(aField, bField, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
        shork.memoryCore.storeAbsolute(address, dat)
    }

    @Test
    fun `test modifier A fields equal`() {
        val seq = setupSeq(Modifier.A)
        setupMemory(42, 0, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier A fields not equal`() {
        val seq = setupSeq(Modifier.A)
        setupMemory(40, 0, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun `test modifier B fields equal`() {
        val seq = setupSeq(Modifier.B)
        setupMemory(0, 69, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier B fields not equal`() {
        val seq = setupSeq(Modifier.B)
        setupMemory(0, 40, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun `test modifier F fields equal`() {
        val seq = setupSeq(Modifier.F)
        setupMemory(42, 69, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier F fields not equal`() {
        val seq = setupSeq(Modifier.F)
        setupMemory(42, 69, 1)
        setupMemory(69, 42, 2)
        executeSeqAndAssertCounter(seq, 0)

        setupMemory(42, 69, 1)
        setupMemory(42, 70, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun `test modifier I equal`() {
        val seq = setupSeq(Modifier.I)
        setupMemory(42, 69, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier I not equal`() {
        val seq = setupSeq(Modifier.I)
        setupMemory(42, 69, 1)
        setupMemory(69, 42, 2)
        executeSeqAndAssertCounter(seq, 0) // no match, so skip next instruction
    }

    @Test
    fun `test modifier AB equal`() {
        val seq = setupSeq(Modifier.AB)
        setupMemory(42, 69, 1)
        setupMemory(69, 42, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier AB not equal`() {
        val seq = setupSeq(Modifier.AB)
        setupMemory(42, 69, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun `test modifier BA equal`() {
        val seq = setupSeq(Modifier.BA)
        setupMemory(69, 42, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier BA not equal`() {
        val seq = setupSeq(Modifier.BA)
        setupMemory(69, 42, 1)
        setupMemory(69, 42, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun `test modifier X equal`() {
        val seq = setupSeq(Modifier.X)
        setupMemory(42, 69, 1)
        setupMemory(69, 42, 2)
        executeSeqAndAssertCounter(seq, 1)
    }

    @Test
    fun `test modifier X not equal`() {
        val seq = setupSeq(Modifier.X)
        setupMemory(42, 69, 1)
        setupMemory(42, 69, 2)
        executeSeqAndAssertCounter(seq, 0)

        setupMemory(42, 69, 1)
        setupMemory(70, 42, 2)
        executeSeqAndAssertCounter(seq, 0)
    }

    @Test
    fun testDeepCopy() {
        val seq = Seq(42, 69, AddressMode.DIRECT, AddressMode.IMMEDIATE, Modifier.A)
        val copy = seq.deepCopy()

        assertEquals(seq.aField, copy.aField)
        assertEquals(seq.bField, copy.bField)
        assertEquals(seq.addressModeA, copy.addressModeA)
        assertEquals(seq.addressModeB, copy.addressModeB)
        assertEquals(seq.modifier, copy.modifier)
        assert(seq !== copy)
    }
}
