package mocks

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

/**
 * Instruction that is used for testing; Removes all processes in a program, effectively making it
 * loose / killing it. 🌹⚰️🪦
 */
internal class KillProgramInstruction(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    constructor() : this(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.A)

    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        while (!process.program.processes.isEmpty()) {
            process.program.processes.removeByReference(process.program.processes.get())
        }
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return KillProgramInstruction(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return KillProgramInstruction(aField, bField, addressModeA, addressModeB, modifier)
    }
}
