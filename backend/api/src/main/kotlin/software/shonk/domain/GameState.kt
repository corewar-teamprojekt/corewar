package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
enum class GameState {
    NOT_STARTED,
    RUNNING,
    FINISHED,
}
