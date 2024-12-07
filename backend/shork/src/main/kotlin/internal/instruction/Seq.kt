package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Seq(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val core = process.program.shork.memoryCore

        val sourceAddress = core.resolveForReading(process.programCounter, aField, addressModeA)
        val destinationAddress =
            core.resolveForReading(process.programCounter, bField, addressModeB)
        val sourceInstruction = core.loadAbsolute(sourceAddress)
        val destinationInstruction = core.loadAbsolute(destinationAddress)

        val match =
            when (modifier) {
                Modifier.A -> {
                    sourceInstruction.aField == destinationInstruction.aField
                }
                Modifier.B -> {
                    sourceInstruction.bField == destinationInstruction.bField
                }
                Modifier.AB -> {
                    sourceInstruction.aField == destinationInstruction.bField
                }
                Modifier.BA -> {
                    sourceInstruction.bField == destinationInstruction.aField
                }
                Modifier.F -> {
                    sourceInstruction.aField == destinationInstruction.aField &&
                        sourceInstruction.bField == destinationInstruction.bField
                }
                Modifier.X -> {
                    sourceInstruction.aField == destinationInstruction.bField &&
                        sourceInstruction.bField == destinationInstruction.aField
                }
                Modifier.I -> {
                    sourceInstruction::class == destinationInstruction::class &&
                        sourceInstruction.aField == destinationInstruction.aField &&
                        sourceInstruction.bField == destinationInstruction.bField &&
                        sourceInstruction.modifier == destinationInstruction.modifier
                }
            }

        if (match) {
            process.programCounter++
        }
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return Seq(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Seq(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[SEQ] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
