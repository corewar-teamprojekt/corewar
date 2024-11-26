package software.shonk.application.service

import kotlin.getOrThrow
import software.shonk.application.port.incoming.V0ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

class V0ShorkService(private val shork: IShork) : V0ShorkUseCase {
    private var programs = HashMap<String, String>()

    private var gameState: GameState = GameState.NOT_STARTED
    private var winner: Winner = Winner.UNDECIDED

    private var currentSettings: Settings = Settings()

    override fun setSettings(settings: Settings) {
        currentSettings = settings
    }

    override fun addProgram(name: String, program: String) {
        if (gameState == GameState.FINISHED) {
            cleanup()
        }
        programs[name] = program
        if (containsPlayerAAndB()) {
            run()
        }
    }

    override fun run() {
        gameState = GameState.RUNNING
        winner = Winner.UNDECIDED

        val gameOutcome = shork.run(currentSettings, programs).getOrThrow()
        val winningPlayer = gameOutcome.outcome.player
        gameState = GameState.FINISHED

        if (winningPlayer == "playerA") {
            winner = Winner.A
        } else if (winningPlayer == "playerB") {
            winner = Winner.B
        }

        programs.clear()
    }

    override fun getStatus(): Status {
        return Status(
            programs.containsKey("playerA"),
            programs.containsKey("playerB"),
            gameState,
            Result(winner),
        )
    }

    private fun containsPlayerAAndB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }

    private fun cleanup() {
        winner = Winner.UNDECIDED
        gameState = GameState.NOT_STARTED
    }
}
