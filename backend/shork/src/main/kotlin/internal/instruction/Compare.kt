package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Compare(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val addressA = resolve(process, aField, addressModeA)
        val addressB = resolve(process, bField, addressModeB)
        val aInstruction = process.program.shork.memoryCore.loadAbsolute(addressA)
        val bInstruction = process.program.shork.memoryCore.loadAbsolute(addressB)

        when (modifier) {
            Modifier.A -> {
                if (aInstruction.aField == bInstruction.aField) {
                    process.programCounter += 1
                }
            }
            Modifier.B -> {
                if (aInstruction.bField == bInstruction.bField) {
                    process.programCounter += 1
                }
            }
            Modifier.AB -> {
                if (aInstruction.aField == bInstruction.bField) {
                    process.programCounter += 1
                }
            }
            Modifier.BA -> {
                if (aInstruction.bField == bInstruction.aField) {
                    process.programCounter += 1
                }
            }
            Modifier.F -> {
                if (
                    aInstruction.aField == bInstruction.aField &&
                        aInstruction.bField == bInstruction.bField
                ) {
                    process.programCounter += 1
                }
            }
            Modifier.X -> {
                if (
                    aInstruction.aField == bInstruction.bField &&
                        aInstruction.bField == bInstruction.aField
                ) {
                    process.programCounter += 1
                }
            }
            Modifier.I -> {
                if (
                    aInstruction::class == bInstruction::class &&
                        aInstruction.modifier == bInstruction.modifier &&
                        aInstruction.addressModeA == bInstruction.addressModeA &&
                        aInstruction.aField == bInstruction.aField &&
                        aInstruction.addressModeB == bInstruction.addressModeB &&
                        aInstruction.bField == bInstruction.bField
                ) {
                    process.programCounter += 1
                }
            }
        }
    }

    override fun deepCopy(): AbstractInstruction {
        return Compare(aField, bField, addressModeA, addressModeB, modifier)
    }
}
