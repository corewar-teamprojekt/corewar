package software.shonk.interpreter.internal.program

import software.shonk.interpreter.internal.IInternalShork
import software.shonk.interpreter.internal.process.AbstractProcess

/**
 * Abstract class representing a program that can be run on the Shonk interpreter It holds processes
 * which themselves execute instructions
 */
internal abstract class AbstractProgram(val id: String, val shork: IInternalShork) {
    val processes: MutableList<AbstractProcess> = mutableListOf()

    /**
     * This function will be called once on every turn and should call the tick method of a process
     * contained within the program. If no more processes are remaining it will do nothing.
     */
    abstract fun tick()

    /**
     * This function adds a process to the program to be managed by it. The first process to be
     * executed should also be added this way.
     *
     * @param startingAddress The address in memory where the process should start executing
     */
    abstract fun addProcess(startingAddress: Int)

    /**
     * This function removes a process from the program
     *
     * @param process The process to remove
     */
    abstract fun removeProcess(process: AbstractProcess)
}
