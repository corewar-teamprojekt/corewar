package software.shonk.interpreter.internal.process

import software.shonk.interpreter.internal.program.AbstractProgram

internal abstract class AbstractProcess(val program: AbstractProgram, var programCounter: Int) {
    /**
     * This flag is used to prevent the program counter from being incremented and gets reset at the
     * end of the tick
     */
    var dontIncrementProgramCounter: Boolean = false

    /**
     * This function will be called once on every turn and executes the next instruction in the
     * process
     */
    abstract fun tick()
}
