package software.shonk.application.service

import kotlin.Result
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.*
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Settings
import software.shonk.interpreter.internal.compiler.Compiler

const val NO_LOBBY_MESSAGE = "No lobby with that id"

class ShorkService(private val shork: IShork) : ShorkUseCase {

    val lobbies: HashMap<Long, Lobby> = HashMap()
    private var lobbyCounter = 0L

    override fun setLobbySettings(lobbyId: Long, settings: Settings): Result<Unit> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        lobby.setSettings(settings)
        return Result.success(Unit)
    }

    override fun getLobbySettings(lobbyId: Long): Result<InterpreterSettings> {
        return getLobby(lobbyId).map { lobby ->
            with(lobby.getSettings()) {
                InterpreterSettings(
                    coreSize = coreSize,
                    instructionLimit = instructionLimit,
                    initialInstruction = initialInstruction,
                    maximumTicks = maximumTicks,
                    maximumProcessesPerPlayer = maximumProcessesPerPlayer,
                    readDistance = readDistance,
                    writeDistance = writeDistance,
                    minimumSeparation = minimumSeparation,
                    separation = separation,
                    randomSeparation = randomSeparation,
                )
            }
        }
    }

    override fun addProgramToLobby(lobbyId: Long, name: String?, program: String): Result<Unit> {
        if (lobbies[lobbyId]?.gameState == GameState.FINISHED) {
            lobbies[lobbyId] = resetLobby(lobbyId).getOrThrow()
        }

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

    override fun getLobbyStatus(lobbyId: Long, includeRoundInformation: Boolean): Result<Status> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        val status = lobby.getStatus()
        if (!includeRoundInformation) {
            status.visualizationData = emptyList()
        }

        return Result.success(status)
    }

    override fun getCompilationErrors(code: String): List<CompileError> {
        val compiler = Compiler(code)
        return compiler.allErrors.map {
            CompileError(
                line = it.lineNumber,
                message = it.message,
                columnStart = it.lineCharIndexStart,
                columnEnd = it.lineCharIndexEnd,
            )
        }
    }

    override fun createLobby(playerName: String): Result<Long> {
        if (isAlphaNumerical(playerName)) {
            lobbies[lobbyCounter] = Lobby(lobbyCounter, HashMap(), shork)
            lobbies[lobbyCounter]?.joinedPlayers?.add(playerName)
            return Result.success(lobbyCounter++)
        }
        return Result.failure(IllegalArgumentException("Your player name is invalid"))
    }

    override fun joinLobby(lobbyId: Long, playerName: String): Result<Unit> {
        val lobby =
            getLobby(lobbyId).getOrElse {
                return Result.failure(it)
            }

        if (isAlphaNumerical(playerName) && hasNotJoinedTheLobby(lobbyId, playerName)) {
            lobby.joinedPlayers.add(playerName)
            return Result.success(Unit)
        }
        return Result.failure(
            IllegalArgumentException("Your player name is invalid OR Lobby doesn't exist")
        )
    }

    override fun getAllLobbies(): List<LobbyStatus> {
        return lobbies.keys.mapNotNull { lobbyId ->
            val status = getLobbyStatus(lobbyId, true).getOrNull() ?: return@mapNotNull null
            val playersJoined = lobbies[lobbyId]?.joinedPlayers ?: return@mapNotNull null
            return@mapNotNull LobbyStatus(
                id = lobbyId,
                playersJoined = playersJoined,
                gameState = status.gameState.toString(),
            )
        }
    }

    override fun authenticatePlayer(playerName: String, lobbyId: Long): Result<Unit> {
        println(lobbies[lobbyId]?.joinedPlayers)
        if (lobbies[lobbyId]?.joinedPlayers?.contains(playerName) == true) {
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("You have not joined a lobby!"))
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

    private fun resetLobby(lobbyId: Long): Result<Lobby> {
        if (lobbies.containsKey(lobbyId)) {
            val newLobby = Lobby(lobbyId, HashMap(), shork)
            lobbies[lobbyId] = newLobby
            return Result.success(newLobby)
        }
        return Result.failure(IllegalArgumentException(NO_LOBBY_MESSAGE))
    }

    private fun hasNotJoinedTheLobby(lobbyId: Long, playerName: String): Boolean {
        var boolean = true
        if (lobbies[lobbyId]?.joinedPlayers?.contains(playerName) == true) {
            boolean = false
        }
        return boolean
    }
}
