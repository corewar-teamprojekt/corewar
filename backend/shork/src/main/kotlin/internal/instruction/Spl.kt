package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Spl(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val startAddress = resolve(process, aField, addressModeA)
        process.program.createProcessAt(startAddress)
    }

    override fun deepCopy(): AbstractInstruction {
        return Spl(aField, bField, addressModeA, addressModeB, modifier)
    }
}
