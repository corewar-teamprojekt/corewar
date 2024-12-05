package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Jmz(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        val core = process.program.shork.memoryCore
        val checkZeroAddress = resolvedAddresses.bFieldRead
        val checkZeroInstruction = core.loadAbsolute(checkZeroAddress)
        val absoluteJumpDestination = resolvedAddresses.aFieldRead

        val shouldJump =
            when (modifier) {
                Modifier.A,
                Modifier.BA -> checkZeroInstruction.aField == 0
                Modifier.B,
                Modifier.AB -> checkZeroInstruction.bField == 0
                Modifier.F,
                Modifier.X,
                Modifier.I -> checkZeroInstruction.aField == 0 && checkZeroInstruction.bField == 0
            }

        if (shouldJump) {
            process.programCounter = absoluteJumpDestination
            process.dontIncrementProgramCounter = true
        }
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return Jmz(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Jmz(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[JMZ] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
