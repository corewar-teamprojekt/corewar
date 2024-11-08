package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Jmp(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val absoluteJumpDestination =
            process.program.shork.memoryCore.resolveForReading(
                process.programCounter,
                aField,
                addressModeA,
            )
        process.programCounter = absoluteJumpDestination
        process.dontIncrementProgramCounter = true
    }

    override fun deepCopy(): AbstractInstruction {
        return Jmp(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[JMP] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
