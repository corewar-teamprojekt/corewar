package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Djn(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val core = process.program.shork.memoryCore
        val checkZeroAddress = core.resolveForReading(process.programCounter, bField, addressModeB)
        val checkZeroInstruction = core.loadAbsolute(checkZeroAddress)
        val absoluteJumpDestination =
            core.resolveForReading(process.programCounter, aField, addressModeA)

        val shouldJump =
            when (modifier) {
                Modifier.A,
                Modifier.BA -> {
                    checkZeroInstruction.aField -= 1
                    checkZeroInstruction.aField != 0
                }
                Modifier.B,
                Modifier.AB -> {
                    checkZeroInstruction.bField -= 1
                    checkZeroInstruction.bField != 0
                }
                Modifier.F,
                Modifier.X,
                Modifier.I -> {
                    checkZeroInstruction.aField -= 1
                    checkZeroInstruction.bField -= 1
                    checkZeroInstruction.aField != 0 && checkZeroInstruction.bField != 0
                }
            }

        if (shouldJump) {
            process.programCounter = absoluteJumpDestination
            process.dontIncrementProgramCounter = true
        }
    }

    override fun deepCopy(): AbstractInstruction {
        return Djn(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[DJN] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
