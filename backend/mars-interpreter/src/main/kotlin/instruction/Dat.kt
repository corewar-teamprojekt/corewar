package software.shonk.interpreter.instruction

import software.shonk.interpreter.AddressMode
import software.shonk.interpreter.Modifier
import software.shonk.interpreter.process.AbstractProcess

class Dat(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        process.program.removeProcess(process)
    }
}
