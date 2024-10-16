package software.shonk.interpreter.instruction

import software.shonk.interpreter.AddressMode
import software.shonk.interpreter.Modifier
import software.shonk.interpreter.process.AbstractProcess

class Jump(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val absoluteJumpDestination = resolve(process, aField, addressModeA)
        process.programCounter = absoluteJumpDestination
    }
}
