package software.shonk.interpreter.internal.settings

import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.statistics.GameDataCollector
import software.shonk.interpreter.internal.statistics.IGameDataCollector

internal class InternalSettings(
    /** The size of the core, in number of instructions it will fit. */
    val coreSize: Int,
    /**
     * The maximum number of instructions will be executed before the interpreter stops and a draw
     * is called.
     */
    val instructionLimit: Int,
    /** The initial instruction the core will be filled with. */
    val initialInstruction: AbstractInstruction,
    /**
     * The maximum number of cycles the interpreter will run before stopping. A cycle is an
     * execution of a single instruction of every players' program.
     */
    val maximumCycles: Int,
    /** The minimum separation between two processes. */
    val minimumSeparation: Int,

    /** The maximum number of processes that can be running at the same time. */
    val maximumProcessesPerPlayer: Int,

    /** The game data collector to use. */
    val gameDataCollector: IGameDataCollector = GameDataCollector(),
)
