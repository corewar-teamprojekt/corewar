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
    private var visualizationData = emptyList<RoundInformation>()

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
            visualizationData = visualizationData,
        )

    fun addProgram(name: String, program: String) {
        programs.put(name, program)
        if (containsPlayerAandB()) {
            run()
        }
    }

    fun run() {
        val result = shork.run(currentSettings, programs).getOrThrow()
        visualizationData = result.roundInformation.map { it.toDomainRoundInformation() }
        val winningPlayer = result.outcome.player
        gameState = GameState.FINISHED

        if (winningPlayer == "playerA") {
            winner = Winner.A
        } else if (winningPlayer == "playerB") {
            winner = Winner.B
        }
    }

    fun containsPlayerAandB(): Boolean {
        return programs.containsKey("playerA") && programs.containsKey("playerB")
    }
}
