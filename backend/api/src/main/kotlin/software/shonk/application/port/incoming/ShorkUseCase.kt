package software.shonk.application.port.incoming

import software.shonk.domain.Status
import software.shonk.interpreter.Settings

interface ShorkUseCase {
    fun addProgramToLobby(lobbyId: Long, name: String, program: String)

    fun setLobbySettings(lobbyId: Long, settings: Settings)

    fun getLobbyStatus(lobbyId: Long): Status?
}
