package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkInterpreterUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.Settings

class MockShorkInterpreterService() : ShorkInterpreterUseCase {
    val shork = MockShork()

    var programs = HashMap<String, String>()
    var currentSettings: Settings = Settings(42, 100, "DAT", 100)

    var gameState = GameState.NOT_STARTED
    var winner = Winner.UNDECIDED

    internal fun containsPlayerAAndB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }

    override fun addProgram(name: String, program: String) {
        programs.put(name, program)
        if (containsPlayerAAndB()) {
            run()
        }
    }

    override fun setSettings(settings: Settings) {
        TODO("Not yet implemented")
    }

    override fun run() {
        gameState = GameState.NOT_STARTED
        winner = Winner.UNDECIDED

        val result = shork.run(currentSettings, programs)
        gameState = GameState.FINISHED

        if (result == "A") {
            winner = Winner.A
        } else if (result == "B") {
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
}
