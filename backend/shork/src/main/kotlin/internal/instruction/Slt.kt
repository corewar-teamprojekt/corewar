package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Slt(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val core = process.program.shork.memoryCore
        val sourceAddress = resolve(process, aField, addressModeA)
        val destinationAddress = resolve(process, bField, addressModeB)
        val sourceInstruction = core.loadAbsolute(sourceAddress)
        val destinationInstruction = core.loadAbsolute(destinationAddress)

        val match =
            when (modifier) {
                Modifier.A -> {
                    sourceInstruction.aField < destinationInstruction.aField
                }
                Modifier.B -> {
                    sourceInstruction.bField < destinationInstruction.bField
                }
                Modifier.AB -> {
                    sourceInstruction.aField < destinationInstruction.bField
                }
                Modifier.BA -> {
                    sourceInstruction.bField < destinationInstruction.aField
                }
                Modifier.F,
                Modifier.I -> {
                    sourceInstruction.aField < destinationInstruction.aField &&
                        sourceInstruction.bField < destinationInstruction.bField
                }
                Modifier.X -> {
                    sourceInstruction.aField < destinationInstruction.bField &&
                        sourceInstruction.bField < destinationInstruction.aField
                }
            }

        if (match) {
            process.programCounter++
        }
    }

    override fun deepCopy(): AbstractInstruction {
        return Slt(aField, bField, addressModeA, addressModeB, modifier)
    }
}
