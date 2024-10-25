package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

class ShorkService(val shork: IShork) : ShorkUseCase {
    var programs = HashMap<String, String>()

    var gameState: GameState = GameState.NOT_STARTED
    var winner: Winner = Winner.UNDECIDED

    var currentSettings: Settings = Settings(42, 100, "DAT", 100)

    override fun setSettings(settings: Settings) {
        this.currentSettings = settings
    }

    override fun addProgram(name: String, program: String) {
        programs.put(name, program)
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

    internal fun containsPlayerAAndB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }
}
