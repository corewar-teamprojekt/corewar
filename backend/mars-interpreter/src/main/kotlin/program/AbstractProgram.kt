package software.shonk.interpreter.program

import software.shonk.interpreter.IShork

/**
 * Abstract class representing a program that can be run on the Shonk interpreter It holds processes
 * which themselves execute instructions
 */
// TODO: Add List of processes
// TODO: Accept source code for the starting / main process
abstract class AbstractProgram(val id: String, val shork: IShork) {
    /**
     * This function will be called once on every turn and should execute a single instruction from
     * one of the processes managed by the program
     */
    abstract fun tick()

    /**
     * This function adds a process to the program to be managed by it
     *
     * @param startingAddress The address in memory where the process should start executing
     */
    // TODO: Should the caller be responsible for constructing an AbstractProcess? Factory?
    abstract fun addProcess(startingAddress: Int)
}
