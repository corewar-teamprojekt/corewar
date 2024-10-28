package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier

internal class Mod(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractArithmeticInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun runArithmeticOperation(operand1: Int, operand2: Int): Int {
        if (operand2 == 0) {
            throw ArithmeticException("Divide by zero")
        }
        return operand1 % operand2
    }

    override fun deepCopy(): AbstractInstruction {
        return Mod(aField, bField, addressModeA, addressModeB, modifier)
    }
}
