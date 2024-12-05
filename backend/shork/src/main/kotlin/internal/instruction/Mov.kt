package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class Mov(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        val core = process.program.shork.memoryCore

        val sourceAddress = resolvedAddresses.aFieldRead
        val destinationAddress = resolvedAddresses.bFieldWrite
        val sourceInstruction = core.loadAbsolute(sourceAddress)
        val destinationInstruction = core.loadAbsolute(destinationAddress)

        when (modifier) {
            Modifier.A -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.aField = sourceInstruction.aField
                }
            }
            Modifier.B -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.bField = sourceInstruction.bField
                }
            }
            Modifier.AB -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.bField = sourceInstruction.aField
                }
            }
            Modifier.BA -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.aField = sourceInstruction.bField
                }
            }
            Modifier.F -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.aField = sourceInstruction.aField
                    it.bField = sourceInstruction.bField
                }
            }
            Modifier.X -> {
                destinationInstruction.writeToMemory(core, destinationAddress) {
                    it.aField = sourceInstruction.bField
                    it.bField = sourceInstruction.aField
                }
            }
            Modifier.I -> {
                val address = resolvedAddresses.bFieldWrite
                core.storeAbsolute(address, sourceInstruction.deepCopy())
            }
        }
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return Mov(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return Mov(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "[MOV] $addressModeA $aField, $addressModeB $bField, $modifier"
    }
}
