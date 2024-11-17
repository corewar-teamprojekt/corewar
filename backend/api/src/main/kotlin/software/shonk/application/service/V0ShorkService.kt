package software.shonk.application.service

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

    private var currentSettings: Settings = Settings(42, 100, "DAT", 100)

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

        val result: String? = shork.run(currentSettings, programs)
        gameState = GameState.FINISHED

        if (result == "playerA") {
            winner = Winner.A
        } else if (result == "playerB") {
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
