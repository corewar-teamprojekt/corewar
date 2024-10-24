package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.port.outgoing.ShorkPort
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.Settings

class ShorkService(val shorkPort: ShorkPort) : ShorkUseCase {
    var programs = HashMap<String, String>()

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
        this.shorkPort.setSettings(settings)
    }

    override fun run() {
        gameState = GameState.NOT_STARTED
        winner = Winner.UNDECIDED

        val result = shorkPort.run(programs)
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
