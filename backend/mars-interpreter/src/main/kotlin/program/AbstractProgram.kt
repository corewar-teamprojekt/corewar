package software.shonk.interpreter.program

import software.shonk.interpreter.IShork

/**
 * Abstract class representing a program that can be run on the Shonk interpreter It holds processes
 * which themselves execute instructions
 */
// TODO: Add List of processes
// TODO: Accept source code for the starting / main process
abstract class AbstractProgram(val id: String, val shork: IShork) {
    // This function will be called once on every turn and should execute a single instruction
    // from one of the processes contained in the program
    abstract fun tick()
}
