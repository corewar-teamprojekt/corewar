package instruction

import org.junit.jupiter.api.Test
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Mov
import software.shonk.interpreter.internal.process.Process
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestMov {

    val dat = Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)
    val settings = InternalSettings(8000, 1000, dat, 1000)
    val shork = InternalShork(settings)
    val program = Program("mov", shork)
    val process = Process(program, 0)

    @Test
    fun testExecuteA() {
        val mov1 = Mov(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        val mov2 = Mov(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        shork.memoryCore.storeAbsolute(0, mov1)
        shork.memoryCore.storeAbsolute(1, mov2)

        mov1.execute(process)
        assert(shork.memoryCore.loadAbsolute(2) is Dat)
        assert(shork.memoryCore.loadAbsolute(2).aField == 42)
        assert(shork.memoryCore.loadAbsolute(2).bField == 0)
        assert(shork.memoryCore.loadAbsolute(2).addressModeA == AddressMode.IMMEDIATE)
        assert(shork.memoryCore.loadAbsolute(2).addressModeB == AddressMode.IMMEDIATE)
    }

    @Test
    fun testExecuteB() {
        val mov1 = Mov(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        val mov2 = Mov(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B)
        shork.memoryCore.storeAbsolute(0, mov1)
        shork.memoryCore.storeAbsolute(1, mov2)

        mov1.execute(process)
        assert(shork.memoryCore.loadAbsolute(2) is Dat)
        assert(shork.memoryCore.loadAbsolute(2).aField == 0)
        assert(shork.memoryCore.loadAbsolute(2).bField == 69)
        assert(shork.memoryCore.loadAbsolute(2).addressModeA == AddressMode.IMMEDIATE)
        assert(shork.memoryCore.loadAbsolute(2).addressModeB == AddressMode.IMMEDIATE)
    }

    @Test
    fun testExecuteAB() {
        val mov1 = Mov(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        val mov2 = Mov(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.AB)
        shork.memoryCore.storeAbsolute(0, mov1)
        shork.memoryCore.storeAbsolute(1, mov2)

        mov1.execute(process)
        assert(shork.memoryCore.loadAbsolute(2) is Dat)
        assert(shork.memoryCore.loadAbsolute(2).aField == 0)
        assert(shork.memoryCore.loadAbsolute(2).bField == 42)
        assert(shork.memoryCore.loadAbsolute(2).addressModeA == AddressMode.IMMEDIATE)
        assert(shork.memoryCore.loadAbsolute(2).addressModeB == AddressMode.IMMEDIATE)
    }

    @Test
    fun testExecuteBA() {
        val mov1 = Mov(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        val mov2 = Mov(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.BA)
        shork.memoryCore.storeAbsolute(0, mov1)
        shork.memoryCore.storeAbsolute(1, mov2)

        mov1.execute(process)
        assert(shork.memoryCore.loadAbsolute(2) is Dat)
        assert(shork.memoryCore.loadAbsolute(2).aField == 69)
        assert(shork.memoryCore.loadAbsolute(2).bField == 0)
        assert(shork.memoryCore.loadAbsolute(2).addressModeA == AddressMode.IMMEDIATE)
        assert(shork.memoryCore.loadAbsolute(2).addressModeB == AddressMode.IMMEDIATE)
    }

    @Test
    fun testExecuteF() {
        val mov1 = Mov(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        val mov2 = Mov(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)
        shork.memoryCore.storeAbsolute(0, mov1)
        shork.memoryCore.storeAbsolute(1, mov2)

        mov1.execute(process)
        assert(shork.memoryCore.loadAbsolute(2) is Dat)
        assert(shork.memoryCore.loadAbsolute(2).aField == 42)
        assert(shork.memoryCore.loadAbsolute(2).bField == 69)
    }

    @Test
    fun testExecuteI() {
        val mov = Mov(0, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I)
        shork.memoryCore.storeAbsolute(0, mov)

        mov.execute(process)
        assert(shork.memoryCore.loadAbsolute(1) is Mov)
        assert(shork.memoryCore.loadAbsolute(1).aField == 0)
        assert(shork.memoryCore.loadAbsolute(1).bField == 1)
        assert(shork.memoryCore.loadAbsolute(1).addressModeA == AddressMode.DIRECT)
        assert(shork.memoryCore.loadAbsolute(1).addressModeB == AddressMode.DIRECT)
    }

    @Test
    fun testExecuteX() {
        val mov = Mov(0, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X)
        shork.memoryCore.storeAbsolute(0, mov)

        mov.execute(process)
        assert(shork.memoryCore.loadAbsolute(1) is Dat)
        assert(shork.memoryCore.loadAbsolute(1).aField == 1)
        assert(shork.memoryCore.loadAbsolute(1).bField == 0)
        assert(shork.memoryCore.loadAbsolute(1).addressModeA == AddressMode.IMMEDIATE)
        assert(shork.memoryCore.loadAbsolute(1).addressModeB == AddressMode.IMMEDIATE)
    }
}
