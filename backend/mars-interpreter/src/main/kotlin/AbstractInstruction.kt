package software.shonk.interpreter

abstract class AbstractInstruction(
    private val aField: Int,
    private val bField: Int,
    private val addressModeA: AddressMode,
    private val addressModeB: AddressMode,
    private val modifier: Modifier,
) {
    abstract fun execute(program: AbstractProgram, process: AbstractProcess)
}
