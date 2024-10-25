package software.shonk.interpreter

/** Interface for the S.H.O.R.K interpreter This is the main interface for the interpreter */
interface IShork {
    /**
     * Starts the interpreter. The interpreter will run until the programs have finished executing
     * or a maximum execution count occurs
     *
     * @param programs The programs to run. The key is the name of the program (Player) , and the
     *   value is the source code for the program itself. It will be compiled and run by the
     *   interpreter.
     * @return The name / id of the winning program / player
     * @see Settings.instructionLimit
     */
    fun run(settings: Settings, programs: Map<String, String>): String?
}
