package mocks

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.process.AbstractProcess

internal class MockInstruction(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        // Nothing here :3
    }

    override fun deepCopy(): AbstractInstruction {
        return MockInstruction(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "MockInstruction(aField=$aField, bField=$bField, addressModeA=$addressModeA, addressModeB=$addressModeB, modifier=$modifier)"
    }
}
