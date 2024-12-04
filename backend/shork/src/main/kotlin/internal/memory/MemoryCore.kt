package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.settings.InternalSettings

internal class MemoryCore(memorySize: Int, val settings: InternalSettings) : ICore {
    private val memory: Array<AbstractInstruction> =
        Array(memorySize) { settings.initialInstruction.deepCopy() }

    override fun loadAbsolute(address: Int): AbstractInstruction {
        val resolvedAddress = resolvedAddressBounds(address)
        settings.gameDataCollector.collectMemoryRead(resolvedAddress)
        return memory[resolvedAddress]
    }

    override fun storeAbsolute(address: Int, instruction: AbstractInstruction) {
        val resolvedAddress = resolvedAddressBounds(address)
        settings.gameDataCollector.collectMemoryWrite(resolvedAddress, instruction)
        memory[resolvedAddress] = instruction
    }

    override fun resolveForReading(sourceAddress: Int, field: Int, mode: AddressMode): Int {
        val maxDistance = settings.readDistance
        return resolve(sourceAddress, field, mode, maxDistance)
    }

    override fun resolveForWriting(sourceAddress: Int, field: Int, mode: AddressMode): Int {
        val maxDistance = settings.writeDistance
        return resolve(sourceAddress, field, mode, maxDistance)
    }

    private fun resolvedAddressBounds(address: Int): Int {
        var temp = address % memory.size
        while (temp < 0) {
            temp += memory.size
            temp %= memory.size
        }

        return temp
    }

    /**
     * Resolve the absolute address of a field based on the address mode and the content of the
     * field. In case the AddressMode is one of the Pre/Post In/Decrement modes, the resolve
     * function **will** modify the destination field.
     *
     * @param sourceAddress The address of the instruction that is executing the operation
     * @param field The field to resolve the address of, either the A or B field
     * @param mode The address mode to use, must be the address mode of the field (A or B)
     * @param maxAddressDistance The maximum distance the operation can access
     * @return The resolved absolute address
     */
    private fun resolve(
        sourceAddress: Int,
        field: Int,
        mode: AddressMode,
        maxAddressDistance: Int,
    ): Int {
        val referenceAddress =
            sourceAddress + (field % maxAddressDistance) // Address we are pointing to
        val instruction = loadAbsolute(referenceAddress)

        val addressOffset =
            when (mode) {
                AddressMode.IMMEDIATE -> {
                    0
                }
                AddressMode.DIRECT -> {
                    field
                }
                AddressMode.A_INDIRECT -> {
                    field + instruction.aField
                }
                AddressMode.B_INDIRECT -> {
                    field + instruction.bField
                }
                AddressMode.A_PRE_DECREMENT -> {
                    instruction.writeToMemory(this, sourceAddress) {
                        it.aField -= 1
                        field + it.aField
                    }
                }
                AddressMode.B_PRE_DECREMENT -> {
                    instruction.writeToMemory(this, sourceAddress) {
                        it.bField -= 1
                        field + it.bField
                    }
                }
                AddressMode.A_POST_INCREMENT -> {
                    instruction.writeToMemory(this, sourceAddress) {
                        val offset = field + it.aField
                        it.aField += 1
                        offset
                    }
                }
                AddressMode.B_POST_INCREMENT -> {
                    instruction.writeToMemory(this, sourceAddress) {
                        val offset = field + it.bField
                        it.bField += 1
                        offset
                    }
                }
            }

        return sourceAddress + (addressOffset % maxAddressDistance)
    }
}
