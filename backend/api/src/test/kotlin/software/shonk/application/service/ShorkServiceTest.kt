package software.shonk.application.service

import io.mockk.spyk
import io.mockk.verify
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.domain.*
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.Settings

class ShorkServiceTest {

    @Test
    fun `inits with default lobby with default status`() {
        val shorkService = ShorkService(MockShork())

        assertEquals(shorkService.lobbies.size, 1)
        assertEquals(shorkService.lobbies[0]?.id, 0)

        assertEquals(
            shorkService.getLobbyStatus(0L).getOrThrow(),
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `inits with only one lobby`() {
        val shorkService = ShorkService(MockShork())
        assertEquals(shorkService.lobbies.size, 1)
    }

    @Test
    fun `inits with default lobby and playerA submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals(
            shorkService.getLobbyStatus(0L).getOrThrow(),
            Status(
                playerASubmitted = true,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `inits with default lobby and playerB submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerB", "someProgram")
        assertEquals(
            shorkService.getLobbyStatus(0L).getOrThrow(),
            Status(
                playerASubmitted = false,
                playerBSubmitted = true,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `close lobby removes player code and resets the game state`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        shorkService.addProgramToLobby(0L, "playerB", "someProgram")
        shorkService.resetLobby(0L)
        assertEquals(
            shorkService.getLobbyStatus(0L).getOrThrow(),
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `create lobby creates a new lobby`() {
        val shorkService = ShorkService(MockShork())
        val lobbyId = shorkService.createLobby()
        assertEquals(shorkService.lobbies.size, 2)
        assertEquals(lobbyId, 1)
        assertEquals(
            shorkService.getLobbyStatus(1L).getOrThrow(),
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `upload code in lobby 0 does not affect lobby 1`() {
        val shorkService = ShorkService(MockShork())
        val secondLobby = shorkService.createLobby()
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals(
            shorkService.getLobbyStatus(0L).getOrThrow(),
            Status(
                playerASubmitted = true,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
        assertEquals(
            shorkService.getLobbyStatus(secondLobby).getOrThrow(),
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `add code with playerName null`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.addProgramToLobby(0L, null, "someProgram")
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "Invalid player name")
    }

    @Test
    fun `delete lobby removes the lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.deleteLobby(0L)
        assertEquals(shorkService.lobbies.size, 0)
    }

    @Test
    fun `delete lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.deleteLobby(1L)
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `get status for the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.getLobbyStatus(1L)
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `add program to the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.addProgramToLobby(1L, "playerA", "someProgram")
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `set settings for the lobby`() {
        val shorkService = ShorkService(MockShork())
        val someSettings = Settings(69, 123, "NOP", 0)
        shorkService.setLobbySettings(0, someSettings)
        assertEquals(someSettings, shorkService.lobbies[0]?.getSettings())
    }

    @Test
    fun `set settings for invalid lobby`() {
        val shorkService = ShorkService(MockShork())
        val someSettings = Settings(69, 123, "NOP", 0)
        val result = shorkService.setLobbySettings(1, someSettings)
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `get code from lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        shorkService.addProgramToLobby(0L, "playerB", "someOtherProgram")
        assertEquals("someProgram", shorkService.getProgramFromLobby(0L, "playerA").getOrNull())
        assertEquals(
            "someOtherProgram",
            shorkService.getProgramFromLobby(0L, "playerB").getOrNull(),
        )
    }

    @Test
    fun `get code from multiple independent lobbies`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        shorkService.addProgramToLobby(0L, "playerB", "someOtherProgram")
        val secondLobby = shorkService.createLobby()
        shorkService.addProgramToLobby(secondLobby, "playerA", "differentProgram")
        shorkService.addProgramToLobby(secondLobby, "playerB", "evenMoreDifferentProgram")
        assertEquals("someProgram", shorkService.getProgramFromLobby(0L, "playerA").getOrNull())
        assertEquals(
            "someOtherProgram",
            shorkService.getProgramFromLobby(0L, "playerB").getOrNull(),
        )
        assertEquals(
            "differentProgram",
            shorkService.getProgramFromLobby(secondLobby, "playerA").getOrNull(),
        )
        assertEquals(
            "evenMoreDifferentProgram",
            shorkService.getProgramFromLobby(secondLobby, "playerB").getOrNull(),
        )
    }

    @Test
    fun `get code after game reset`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        shorkService.addProgramToLobby(0L, "playerB", "someOtherProgram")
        // Reset because of new upload
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals("someProgram", shorkService.getProgramFromLobby(0L, "playerA").getOrNull())
        assert(shorkService.getProgramFromLobby(0L, "playerB").isFailure)
    }

    @Test
    fun `get code from lobby with invalid player`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals(
            "No player with that name in the lobby",
            shorkService.getProgramFromLobby(0L, "playerB").exceptionOrNull()?.message,
        )
    }

    @Test
    fun `get code from lobby with invalid lobby`() {
        val shorkService = ShorkService(MockShork())
        assertEquals(
            "No lobby with that id",
            shorkService.getProgramFromLobby(1L, "playerA").exceptionOrNull()?.message,
        )
    }

    @Test
    fun `check if a dead lobby gets closed after new code gets submitted by any player`() {
        val shorkService = spyk(ShorkService(MockShork()))
        val lobbyId = 0L
        shorkService.addProgramToLobby(lobbyId, "playerA", "someProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someOtherProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someNewOtherProgram")
        verify(exactly = 1) { shorkService.resetLobby(lobbyId) }
    }

    @Test
    fun `test get all lobbies`() {
        val shorkService = ShorkService(MockShork())
        var lobbyId = 0L
        shorkService.addProgramToLobby(lobbyId, "playerA", "someOtherProgram")
        shorkService.createLobby()
        shorkService.createLobby()
        lobbyId += 2
        shorkService.addProgramToLobby(lobbyId, "playerA", "someProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someOtherProgram")
        val result = shorkService.getAllLobbies()

        assertEquals(3, result.size)
        result.forEachIndexed { index, lobby ->
            when (index) {
                0 -> {
                    assertEquals(index.toLong(), lobby.lobbyId)
                    assertEquals(listOf("playerA"), lobby.playersJoined)
                    assertEquals(GameState.NOT_STARTED, lobby.gameState)
                }
                1 -> {
                    assertEquals(index.toLong(), lobby.lobbyId)
                    assert(lobby.playersJoined.isEmpty())
                    assertEquals(GameState.NOT_STARTED, lobby.gameState)
                }
                2 -> {
                    assertEquals(index.toLong(), lobby.lobbyId)
                    assertEquals(listOf("playerA", "playerB"), lobby.playersJoined)
                    assertEquals(GameState.FINISHED, lobby.gameState)
                }
            }
        }
    }

    @Test
    fun `get allLobbies but one is deleted`() {
        val shorkService = ShorkService(MockShork())
        var lobbyId = 0L
        shorkService.addProgramToLobby(lobbyId, "playerA", "someOtherProgram")
        shorkService.createLobby()
        shorkService.createLobby()
        lobbyId += 2
        shorkService.addProgramToLobby(lobbyId, "playerA", "someProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someOtherProgram")
        shorkService.createLobby()
        lobbyId++
        shorkService.addProgramToLobby(lobbyId, "playerB", "someOtherProgram")
        shorkService.deleteLobby(lobbyId - 2)

        val result = shorkService.getAllLobbies()

        assertEquals(3, result.size)
        result.forEachIndexed { index, lobby ->
            when (index) {
                0 -> {
                    assertEquals(index.toLong(), lobby.lobbyId)
                    assertEquals(listOf("playerA"), lobby.playersJoined)
                    assertEquals(GameState.NOT_STARTED, lobby.gameState)
                }
                1 -> {
                    assertEquals(index.toLong() + 1, lobby.lobbyId)
                    assertEquals(listOf("playerA", "playerB"), lobby.playersJoined)
                    assertEquals(GameState.FINISHED, lobby.gameState)
                }
                2 -> {
                    assertEquals(index.toLong() + 1, lobby.lobbyId)
                    assertEquals(listOf("playerB"), lobby.playersJoined)
                    assertEquals(GameState.NOT_STARTED, lobby.gameState)
                }
            }
        }
    }
}
