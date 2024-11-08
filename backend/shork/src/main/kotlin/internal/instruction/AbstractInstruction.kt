package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.ICloneable
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
) : ICloneable<AbstractInstruction> {

    abstract fun execute(process: AbstractProcess)

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

    override fun hashCode(): Int {
        var result = aField
        result = 31 * result + bField
        result = 31 * result + addressModeA.hashCode()
        result = 31 * result + addressModeB.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}
