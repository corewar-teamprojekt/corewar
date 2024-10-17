package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

/** Abstract class representing an instruction in the S.H.O.R.K. */
internal abstract class AbstractInstruction(
    var aField: Int,
    var bField: Int,
    var addressModeA: AddressMode,
    var addressModeB: AddressMode,
    var modifier: Modifier,
) {

    abstract fun execute(process: AbstractProcess)

    /**
     * Resolve the absolute address of a field based on the address mode and the content of the
     * field. In case the AddressMode is one of the Pre/Post In/Decrement modes, the resolve
     * function **will** modify the destination field.
     *
     * @param process The process that is executing the instruction
     * @param field The field to resolve the address of, either the A or B field
     * @param mode The address mode to use, must be the address mode of the field (A or B)
     * @return The resolved absolute address
     */
    fun resolve(process: AbstractProcess, field: Int, mode: AddressMode): Int {
        val referenceAddress = process.programCounter + field // Address we are pointing to
        val instruction = process.program.shork.getCore().loadAbsolute(referenceAddress)
        return when (mode) {
            AddressMode.IMMEDIATE -> {
                process.programCounter
            }
            AddressMode.DIRECT -> {
                referenceAddress
            }
            AddressMode.A_INDIRECT -> {
                referenceAddress + instruction.aField
            }
            AddressMode.B_INDIRECT -> {
                referenceAddress + instruction.bField
            }
            AddressMode.A_PRE_DECREMENT -> {
                instruction.aField -= 1
                referenceAddress + instruction.aField
            }
            AddressMode.B_PRE_DECREMENT -> {
                instruction.bField -= 1
                referenceAddress + instruction.bField
            }
            AddressMode.A_POST_INCREMENT -> {
                val address = referenceAddress + instruction.aField
                instruction.aField += 1
                address
            }
            AddressMode.B_POST_INCREMENT -> {
                val address = referenceAddress + instruction.bField
                instruction.bField += 1
                address
            }
        }
    }
}
