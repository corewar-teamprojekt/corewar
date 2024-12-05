package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.instruction.AbstractInstruction

/** The interface for the memory core */
internal interface ICore {
    /** Loads the instruction at the given address */
    fun loadAbsolute(address: Int): AbstractInstruction

    /** Writes an instruction to the given address */
    fun storeAbsolute(address: Int, instruction: AbstractInstruction)

    fun resolveAll(sourceAddress: Int): ResolvedAddresses
}
