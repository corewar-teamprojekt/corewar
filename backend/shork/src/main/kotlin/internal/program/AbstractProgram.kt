package software.shonk.interpreter.internal.program

import software.shonk.interpreter.internal.AbstractInternalShork
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.util.CircularQueue

/**
 * Abstract class representing a program that can be run on the Shork interpreter. It holds
 * processes which themselves execute instructions
 */
internal abstract class AbstractProgram(val playerId: String, val shork: AbstractInternalShork) {
    val processes: CircularQueue<AbstractProcess> = CircularQueue()

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
    abstract fun createProcessAt(startingAddress: Int)

    /**
     * This function removes a process from the program
     *
     * @param process The process to remove
     */
    abstract fun removeProcess(process: AbstractProcess)

    /**
     * This function checks if the program is still alive, i.e. if there are still processes
     * executing / that can execute
     */
    fun isAlive(): Boolean {
        return !processes.isEmpty()
    }
}
