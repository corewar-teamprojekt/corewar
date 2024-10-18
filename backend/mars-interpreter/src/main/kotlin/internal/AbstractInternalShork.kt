package software.shonk.interpreter.internal

import software.shonk.interpreter.GameStatus
import software.shonk.interpreter.internal.memory.MemoryCore
import software.shonk.interpreter.internal.program.AbstractProgram
import software.shonk.interpreter.internal.settings.InternalSettings

/**
 * Abstract class an internal representation for the S.H.O.R.K interpreter. It is not intended to be
 * used by the downstream consumer of the library/interpreter.
 */
internal abstract class AbstractInternalShork(val settings: InternalSettings) {
    val memoryCore = MemoryCore(settings.coreSize, settings.initialInstruction)
        get() = field

    abstract fun addProgram(vararg program: AbstractProgram)

    abstract fun run(): GameStatus
}
