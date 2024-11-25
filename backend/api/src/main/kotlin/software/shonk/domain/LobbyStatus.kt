package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
data class LobbyStatus(val id: Long, val playersJoined: List<String>, val gameState: String)
