package software.shonk.application.service

import kotlin.getOrThrow
import software.shonk.application.port.incoming.V0ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.V0Result
import software.shonk.domain.V0Status
import software.shonk.domain.V0Winner
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

class V0ShorkService(private val shork: IShork) : V0ShorkUseCase {
    private var programs = HashMap<String, String>()

    private var gameState: GameState = GameState.NOT_STARTED
    private var winner: V0Winner = V0Winner.UNDECIDED

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
        winner = V0Winner.UNDECIDED

        val gameOutcome = shork.run(currentSettings, programs).getOrThrow()
        val winningPlayer = gameOutcome.outcome.player
        gameState = GameState.FINISHED

        if (winningPlayer == "playerA") {
            winner = V0Winner.A
        } else if (winningPlayer == "playerB") {
            winner = V0Winner.B
        }

        programs.clear()
    }

    override fun getStatus(): V0Status {
        return V0Status(
            programs.containsKey("playerA"),
            programs.containsKey("playerB"),
            gameState,
            V0Result(winner),
        )
    }

    private fun containsPlayerAAndB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }

    private fun cleanup() {
        winner = V0Winner.UNDECIDED
        gameState = GameState.NOT_STARTED
    }
}
