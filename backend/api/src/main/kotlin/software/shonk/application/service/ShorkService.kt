package software.shonk.application.service

import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.GameState
import software.shonk.domain.Lobby
import software.shonk.domain.Status
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings

const val NO_LOBBY_MESSAGE = "No lobby with that id"

class ShorkService(private val shork: IShork) : ShorkUseCase {

    private var v0Lobby: Lobby? = null
    private var lastV0GameStatus: Status? = null

    val lobbies: HashMap<Long, Lobby> = HashMap<Long, Lobby>()
    private var lobbyCounter = 0L

    override fun setLobbySettings(lobbyId: Long, settings: Settings): Result<Unit> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        lobby.setSettings(settings)
        return Result.success(Unit)
    }

    override fun addProgramToLobby(lobbyId: Long, name: String?, program: String): Result<Unit> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        if (name == null || !verifyPlayerName(name)) {
            return Result.failure(IllegalArgumentException("Invalid player name"))
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

    override fun getV0LobbyStatus(): Status {
        if (lastV0GameStatus != null) {
            return lastV0GameStatus!!
        }
        return v0Lobby?.getStatus() ?: Status.defaultState()
    }

    override fun addProgramToV0Lobby(program: String?, name: String): Result<Unit> {
        if (!verifyPlayerName(name)) {
            return Result.failure(IllegalArgumentException("Invalid player name"))
        }

        if (program == null) {
            return Result.failure(IllegalArgumentException("Program cant be null"))
        }

        if (v0Lobby == null) {
            v0Lobby = Lobby(lobbyCounter++, HashMap(), shork)
            lastV0GameStatus = null
        }
        if (!v0Lobby!!.joinedPlayers.contains(name)) {
            v0Lobby!!.joinedPlayers.add(name)
        }

        v0Lobby!!.addProgram(name, program)

        if (v0Lobby!!.getStatus().gameState == GameState.FINISHED) {
            lastV0GameStatus = v0Lobby!!.getStatus()
            v0Lobby = null
        }

        return Result.success(Unit)
    }

    override fun createLobby(playerName: String): Result<Long> {
        if (isAlphaNumerical(playerName)) {
            lobbies[lobbyCounter] = Lobby(lobbyCounter, HashMap(), shork)
            lobbies[lobbyCounter]?.joinedPlayers?.add(playerName)
            return Result.success(lobbyCounter++)
        }
        return Result.failure(IllegalArgumentException("Your player name is invalid"))
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

    private fun isAlphaNumerical(playerName: String): Boolean {
        return playerName.matches("^[a-zA-Z0-9]+$".toRegex()) && playerName.isNotBlank()
    }
}
