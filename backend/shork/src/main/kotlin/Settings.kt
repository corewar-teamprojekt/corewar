package software.shonk.interpreter

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Dat

/**
 * The settings for the interpreter
 *
 * @param coreSize The size of the core, in number of instructions it will fit.
 * @param instructionLimit The maximum number of instructions will be executed before the
 *   interpreter stops. -1 for no limit
 */
class Settings(
    val coreSize: Int,
    val instructionLimit: Int,
    val initialInstruction: String,
    val maximumTicks: Int,
) {
    internal fun toInternalSettings() =
        software.shonk.interpreter.internal.settings.InternalSettings(
            coreSize,
            instructionLimit,
            Dat(
                42,
                1337,
                AddressMode.DIRECT,
                AddressMode.DIRECT,
                Modifier.I,
            ), // TODO: replace with actual instruction once the parser exists
            maximumTicks,
        )
}
