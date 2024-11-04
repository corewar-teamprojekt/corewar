package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Nop(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun execute(process: AbstractProcess) {}

    override fun deepCopy(): AbstractInstruction {
        return Nop(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[NOP] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
