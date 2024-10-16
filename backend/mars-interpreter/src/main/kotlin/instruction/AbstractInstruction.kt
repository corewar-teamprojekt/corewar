package software.shonk.interpreter.instruction

import software.shonk.interpreter.process.AbstractProcess
import software.shonk.interpreter.program.AbstractProgram
import software.shonk.interpreter.AddressMode
import software.shonk.interpreter.Modifier

abstract class AbstractInstruction(
    private val aField: Int,
    private val bField: Int,
    private val addressModeA: AddressMode,
    private val addressModeB: AddressMode,
    private val modifier: Modifier,
) {
    abstract fun execute(program: AbstractProgram, process: AbstractProcess)
}
