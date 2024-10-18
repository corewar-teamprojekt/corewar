package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkInterpreterUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.settings.AbstractSettings // TODO
import software.shonk.interpreter.settings.MockSettings

// Only do business logic on this layer, potentially calling into outgoing ports (e.g. DB, MARS
// interpreter whatever)
// Work with domain objects on this layer, only converting to DTO in the Controller if needed.
class MockShorkInterpreterService() : ShorkInterpreterUseCase {
    val shork = MockShork()

    var programs = HashMap<String, String>()
    var currentSettings: AbstractSettings = MockSettings()

    var gameState = GameState.NOT_STARTED
    var winner = Winner.UNDECIDED

    internal fun containsPlayerAAndB(): Boolean {
        return programs.containsKey("A") && programs.containsKey("B")
    }

    override fun addProgram(name: String, program: String) {
        programs.put(name, program)
        if (containsPlayerAAndB()) {
            run()
        }
    }

    override fun setSettings(settings: AbstractSettings) {
        TODO("Not yet implemented")
    }

    override fun run() {
        gameState = GameState.NOT_STARTED
        winner = Winner.UNDECIDED

        shork.run(programs, currentSettings)
        gameState = GameState.RUNNING

        val result = shork.getResult()
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
            programs.containsKey("A"),
            programs.containsKey("B"),
            gameState,
            Result(winner),
        )
    }
}
