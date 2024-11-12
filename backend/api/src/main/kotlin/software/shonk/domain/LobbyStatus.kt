package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
data class LobbyStatus(
    val lobbyId: Long,
    val playersJoined: List<String>,
    val gameState: GameState,
)
