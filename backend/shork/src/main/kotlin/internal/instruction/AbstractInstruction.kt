package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.ICloneable
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.MemoryCore
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

/**
 * Mutable version of [AbstractInstruction]. Used to collect changes for copying. Exists only to
 * make copying instruction changes more convenient
 */
internal data class MutableAbstractInstruction(
    var aField: Int,
    var bField: Int,
    var addressModeA: AddressMode,
    var addressModeB: AddressMode,
    var modifier: Modifier,
)

/** Abstract class representing an instruction in the S.H.O.R.K. */
internal abstract class AbstractInstruction(
    val aField: Int,
    val bField: Int,
    val addressModeA: AddressMode,
    val addressModeB: AddressMode,
    val modifier: Modifier,
) : ICloneable<AbstractInstruction> {

    abstract fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractInstruction

        if (aField != other.aField) return false
        if (bField != other.bField) return false
        if (addressModeA != other.addressModeA) return false
        if (addressModeB != other.addressModeB) return false
        if (modifier != other.modifier) return false

        return true
    }

    /**
     * Creates a new instance of the instruction with the changes made by the provided block.
     * Returns the new instruction and whatever the block returns. Does not write the changes to
     * memory.
     *
     * @param block The (lambda) function that makes the changes to the fields
     * @return A pair of the new instruction and whatever the block returns
     */
    fun <T> write(block: (MutableAbstractInstruction) -> T): Pair<AbstractInstruction, T> {
        // Create the mutable instruction "container" to store / work with the changes
        val mutableInstruction =
            MutableAbstractInstruction(aField, bField, addressModeA, addressModeB, modifier)
        // Run the provided code with the context of the instruction
        val res = block(mutableInstruction)

        // Create a new instruction with the changes
        val new =
            newInstance(
                mutableInstruction.aField,
                mutableInstruction.bField,
                mutableInstruction.addressModeA,
                mutableInstruction.addressModeB,
                mutableInstruction.modifier,
            )

        // Return whatever the function wanted to return
        return Pair(new, res)
    }

    /**
     * Creates a new instance of the instruction with the changes made by the provided block. Writes
     * the changes to memory at the given address. Returns whatever the block returns.
     *
     * @param memoryCore The memory core to write the changes to
     * @param instructionAddress The address to write the changes to
     * @param block The (lambda) function that makes the changes to the fields
     * @return Whatever the block function returns
     */
    fun <T> writeToMemory(
        memoryCore: MemoryCore,
        instructionAddress: Int,
        block: (MutableAbstractInstruction) -> T,
    ): T {
        // Create the mutable instruction "container" to store / work with the changes
        val mutableInstruction =
            MutableAbstractInstruction(aField, bField, addressModeA, addressModeB, modifier)
        // Run the provided code with the context of the instruction
        val res = block(mutableInstruction)

        // Create a new instruction with the changes
        val new =
            newInstance(
                mutableInstruction.aField,
                mutableInstruction.bField,
                mutableInstruction.addressModeA,
                mutableInstruction.addressModeB,
                mutableInstruction.modifier,
            )

        // Write the instruction to memory / save the changes
        memoryCore.storeAbsolute(instructionAddress, new)

        // Return whatever the function wanted to return
        return res
    }

    /**
     * Creates a new instance of the instruction with the given fields. Is used for example when
     * modifying an instruction to create a new instruction with the changes.
     */
    abstract fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction

    override fun hashCode(): Int {
        var result = aField
        result = 31 * result + bField
        result = 31 * result + addressModeA.hashCode()
        result = 31 * result + addressModeB.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}
