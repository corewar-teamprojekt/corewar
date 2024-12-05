package mocks

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

internal class MockInstruction(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    constructor() : this(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.I)

    var executionCount = 0

    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        executionCount++
    }

    override fun newInstance(
        aField: Int,
        bField: Int,
        addressModeA: AddressMode,
        addressModeB: AddressMode,
        modifier: Modifier,
    ): AbstractInstruction {
        return MockInstruction(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun deepCopy(): AbstractInstruction {
        return MockInstruction(aField, bField, addressModeA, addressModeB, modifier)
    }

    override fun toString(): String {
        return "MockInstruction(aField=$aField, bField=$bField, addressModeA=$addressModeA, addressModeB=$addressModeB, modifier=$modifier)"
    }
}
