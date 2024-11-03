package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Lobby
import software.shonk.domain.Status
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

class ShorkService(val shork: IShork) : ShorkUseCase {

    val lobbies: HashMap<Long, Lobby> = HashMap<Long, Lobby>()

    init {
        var lobby: Lobby = Lobby(0, HashMap<String, String>(), shork)
        lobbies.put(0, lobby)
    }

    override fun setLobbySettings(lobbyId: Long, settings: Settings) {
        lobbies[0]?.setSettings(settings)
    }

    override fun addProgramToLobby(lobbyId: Long, name: String, program: String) {
        if ((lobbies[0]?.getStatus()?.gameState ?: GameState.NOT_STARTED) == GameState.FINISHED) {
            closeLobby()
        }
        lobbies[0]?.addProgram(name, program)
    }

    override fun getLobbyStatus(lobbyId: Long): Status? {
        return lobbies[0]?.getStatus()
    }

    fun closeLobby() {
        lobbies.remove(0)
        lobbies.put(0L, Lobby(0L, HashMap<String, String>(), shork))
    }
}
