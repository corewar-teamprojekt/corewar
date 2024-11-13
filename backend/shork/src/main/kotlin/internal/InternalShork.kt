package software.shonk.interpreter.internal

import software.shonk.interpreter.internal.program.AbstractProgram
import software.shonk.interpreter.internal.settings.InternalSettings

internal class InternalShork(settings: InternalSettings) : AbstractInternalShork(settings) {
    private val programs = mutableListOf<AbstractProgram>()

    override fun addProgram(vararg program: AbstractProgram) {
        programs.addAll(program)
    }

    override fun run(): GameStatus {
        var ticks = 0
        while (ticks < this.settings.maximumCycles) {
            for (program in programs) {
                gameDataCollector.startRoundForProgram(program)
                program.tick()
                gameDataCollector.endRoundForProgram(program)
            }

            val stillRunningPrograms = getStillRunningPrograms()
            if (stillRunningPrograms.size == 1) {
                val winner = stillRunningPrograms.first()
                return GameStatus.FINISHED(FinishedState.WINNER(winner))
            }

            ticks++
        }

        return GameStatus.FINISHED(FinishedState.DRAW)
    }

    private fun getNumStillRunningPrograms() = getStillRunningPrograms().size

    private fun getStillRunningPrograms() = programs.filter { it.isAlive() }
}
