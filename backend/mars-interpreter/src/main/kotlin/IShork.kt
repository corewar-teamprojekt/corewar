package software.shonk.interpreter

import software.shonk.interpreter.settings.AbstractSettings

/** Interface for the S.H.O.R.K interpreter This is the main interface for the interpreter */
interface IShork {
    /**
     * Starts the interpreter. The interpreter will run until the programs have finished executing
     * or a maximum execution count occurs
     *
     * @param programs The programs to run. The key is the name of the program (Player) , and the
     *   value is the source code for the program itself. It will be compiled and run by the
     *   interpreter.
     * @see Settings.instructionLimit
     */
    fun run(programs: Map<String, String>, settings: AbstractSettings)
}
