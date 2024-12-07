package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Spl(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        val startAddress = resolvedAddresses.aFieldRead
        process.program.createProcessAt(startAddress)
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return Spl(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Spl(aField, bField, addressModeA, addressModeB, modifier)
    }
}
