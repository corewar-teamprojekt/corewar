package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.instruction.AbstractInstruction

/** The interface for the memory core */
internal interface ICore {
    /** Loads the instruction at the given address */
    fun loadAbsolute(address: Int): AbstractInstruction

    /** Writes an instruction to the given address */
    fun storeAbsolute(address: Int, instruction: AbstractInstruction)

    /** Resolves the address of a field based on the address mode and the content of the field */
    fun resolveForReading(sourceAddress: Int, field: Int, mode: AddressMode): Int

    /** Resolves the address of a field based on the address mode and the content of the field */
    fun resolveForWriting(sourceAddress: Int, field: Int, mode: AddressMode): Int
}
