package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Slt(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        val core = process.program.shork.memoryCore
        val sourceAddress = resolvedAddresses.aFieldRead
        val destinationAddress = resolvedAddresses.bFieldRead
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

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return Slt(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Slt(aField, bField, addressModeA, addressModeB, modifier)
    }
}
