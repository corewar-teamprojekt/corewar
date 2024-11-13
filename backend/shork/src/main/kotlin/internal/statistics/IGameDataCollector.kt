package software.shonk.interpreter.internal.statistics

import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.program.AbstractProgram

internal interface IGameDataCollector {
    /**
     * Marks the beginning of a round for a program (player). Must be called before the program is
     * ticked.
     */
    fun startRoundForProgram(program: AbstractProgram)

    /**
     * Marks the end of a round for a program (player). Must be called after the program has been
     * ticked.
     */
    fun endRoundForProgram(program: AbstractProgram)

    /** Collects data about a process before it is ticked. */
    fun collectProcessDataBeforeTick(process: AbstractProcess)

    /** Collects data about a process after it is ticked. */
    fun collectProcessDataAfterTick(process: AbstractProcess)

    /** Collects data about a read from memory. */
    fun collectMemoryRead(absoluteAddress: Int)

    /** Collects data about a write to memory. */
    fun collectMemoryWrite(absoluteAddress: Int, instruction: AbstractInstruction)

    /** Returns the information about the rounds collected so far. */
    fun getGameStatistics(): List<RoundInformation>
}
