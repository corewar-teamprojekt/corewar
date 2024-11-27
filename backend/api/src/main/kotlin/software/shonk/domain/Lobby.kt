package software.shonk.domain

import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

data class Lobby(
    val id: Long,
    val programs: HashMap<String, String>,
    val shork: IShork,
    var gameState: GameState = GameState.NOT_STARTED,
    var winner: Winner = Winner.DRAW,
    var currentSettings: Settings = Settings(),
    var joinedPlayers: MutableList<String> = mutableListOf(),
) {
    fun setSettings(settings: Settings) {
        currentSettings = settings
    }

    fun getSettings(): Settings {
        return currentSettings
    }

    fun getStatus() =
        Status(
            playerASubmitted = programs.containsKey("playerA"),
            playerBSubmitted = programs.containsKey("playerB"),
            gameState = gameState,
            result = GameResult(winner),
        )

    fun addProgram(name: String, program: String) {
        programs.put(name, program)
        if (containsPlayerAandB()) {
            run()
        }
    }

    fun run() {
        val result = shork.run(currentSettings, programs).getOrThrow().outcome.player
        gameState = GameState.FINISHED

        if (result == "playerA") {
            winner = Winner.A
        } else if (result == "playerB") {
            winner = Winner.B
        }
    }

    fun containsPlayerAandB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }
}
