package software.shonk.application.port

import kotlinx.serialization.Serializable
import software.shonk.interpreter.settings.AbstractSettings

interface ShorkInterpreterUseCase {
    fun addProgram(name: String, program: String)

    fun setSettings(settings: AbstractSettings)

    fun run()

    fun getStatus(): Status
}

@Serializable
enum class GameState {
    NOT_STARTED,
    RUNNING,
    FINISHED,
}

@Serializable
enum class Winner {
    A,
    B,
    UNDECIDED,
}

@Serializable data class Result(val winner: Winner)

@Serializable
data class Status(
    val playerASubmitted: Boolean,
    val playerBSubmitted: Boolean,
    val gameState: GameState,
    val result: Result,
)
