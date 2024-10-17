package software.shonk.interpreter.settings

/**
 * The settings for the interpreter
 *
 * @param coreSize The size of the core, in number of instructions it will fit.
 * @param instructionLimit The maximum number of instructions will be executed before the
 *   interpreter stops. -1 for no limit
 */
abstract class AbstractSettings(val coreSize: Int, val instructionLimit: Int)
