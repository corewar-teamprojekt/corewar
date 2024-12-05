package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Djn(
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
                Modifier.BA -> {
                    checkZeroInstruction.writeToMemory(core, checkZeroAddress) {
                        it.aField -= 1

                        it.aField != 0
                    }
                }
                Modifier.B,
                Modifier.AB -> {
                    checkZeroInstruction.writeToMemory(core, checkZeroAddress) {
                        it.bField -= 1

                        it.bField != 0
                    }
                }
                Modifier.F,
                Modifier.X,
                Modifier.I -> {
                    checkZeroInstruction.writeToMemory(core, checkZeroAddress) {
                        it.aField -= 1
                        it.bField -= 1

                        it.aField != 0 && it.bField != 0
                    }
                }
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
        return Djn(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Djn(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[DJN] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
