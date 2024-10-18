package software.shonk.interpreter.internal.settings

import software.shonk.interpreter.internal.instruction.AbstractInstruction

internal class InternalSettings(
    val coreSize: Int,
    val instructionLimit: Int,
    val initialInstruction: AbstractInstruction,
    val maximumTicks: Int,
)
