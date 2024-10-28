package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier

internal class Mul(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractArithmeticInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun runArithmeticOperation(operand1: Int, operand2: Int): Int {
        return operand1 * operand2
    }

    override fun deepCopy(): AbstractInstruction {
        return Mul(aField, bField, addressModeA, addressModeB, modifier)
    }
}
