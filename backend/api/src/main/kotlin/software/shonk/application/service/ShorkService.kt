package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Lobby
import software.shonk.domain.Status
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

const val NO_LOBBY_MESSAGE = "No lobby with that id"

class ShorkService(private val shork: IShork) : ShorkUseCase {

    val lobbies: HashMap<Long, Lobby> = HashMap<Long, Lobby>()
    private var lobbyCounter = 0L

    init {
        createLobby()
    }

    override fun setLobbySettings(lobbyId: Long, settings: Settings): Result<Unit> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        lobby.setSettings(settings)
        return Result.success(Unit)
    }

    override fun addProgramToLobby(lobbyId: Long, name: String?, program: String): Result<Unit> {
        var lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        if (name == null || !verifyPlayerName(name)) {
            return Result.failure(IllegalArgumentException("Invalid player name"))
        }

        if (lobby.getStatus().gameState == GameState.FINISHED) {
            // It's fine to throw the exception here,
            // as we already made sure the lobby with the id exists
            lobby = resetLobby(lobbyId).getOrThrow()
        }

        lobby.addProgram(name, program)
        return Result.success(Unit)
    }

    override fun getProgramFromLobby(lobbyId: Long, name: String?): Result<String> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        val result = lobby.programs[name]
        return if (result == null) {
            Result.failure(IllegalArgumentException("No player with that name in the lobby"))
        } else {
            Result.success(result)
        }
    }

    override fun getLobbyStatus(lobbyId: Long): Result<Status> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }
        return Result.success(lobby.getStatus())
    }

    fun resetLobby(lobbyId: Long): Result<Lobby> {
        if (lobbies.containsKey(lobbyId)) {
            val newLobby = Lobby(lobbyId, HashMap(), shork)
            lobbies[lobbyId] = newLobby
            return Result.success(newLobby)
        }
        return Result.failure(IllegalArgumentException(NO_LOBBY_MESSAGE))
    }

    override fun createLobby(): Long {
        lobbies[lobbyCounter] = Lobby(lobbyCounter, HashMap(), shork)
        return lobbyCounter++
    }

    override fun deleteLobby(lobbyId: Long): Result<Unit> {
        if (lobbies.containsKey(lobbyId)) {
            lobbies.remove(lobbyId)
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException(NO_LOBBY_MESSAGE))
    }

    private fun verifyPlayerName(player: String?): Boolean {
        return player == "playerA" || player == "playerB"
    }

    private fun getLobby(lobbyId: Long): Result<Lobby> {
        return lobbies[lobbyId]?.let { Result.success(it) }
            ?: Result.failure(IllegalArgumentException(NO_LOBBY_MESSAGE))
    }
}
