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
    /** The size of the core, in number of instructions it will fit. */
    val coreSize: Int = 8192,
    /**
     * The maximum number of instructions will be executed before the interpreter stops and a draw
     * is called.
     */
    val instructionLimit: Int = 100000,
    /** The initial instruction the core will be filled with. */
    val initialInstruction: String = "DAT $0, $0",
    /**
     * The maximum number of cycles the interpreter will run before stopping. A cycle is an
     * execution of a single instruction of every players' program.
     */
    val maximumTicks: Int = 80000,
    /** The minimum separation between two processes. */
    val mininumSeparation: Int = 100,
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
            mininumSeparation,
        )
}
