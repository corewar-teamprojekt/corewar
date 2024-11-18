package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier

internal class Div(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractArithmeticInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun runArithmeticOperation(
        sourceInstructionOperand: Int,
        destinationInstructionOperand: Int,
    ): Int {
        if (destinationInstructionOperand == 0) {
            throw ArithmeticException("Divide by zero")
        }
        return sourceInstructionOperand / destinationInstructionOperand
    }

    override fun deepCopy(): AbstractInstruction {
        return Div(aField, bField, addressModeA, addressModeB, modifier)
    }
}
