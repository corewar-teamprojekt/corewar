package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val playerASubmitted: Boolean,
    val playerBSubmitted: Boolean,
    val gameState: GameState,
    val result: Result,
) {
    companion object Factory {
        fun defaultState() =
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                GameState.NOT_STARTED,
                result = Result(Winner.UNDECIDED),
            )
    }
}
