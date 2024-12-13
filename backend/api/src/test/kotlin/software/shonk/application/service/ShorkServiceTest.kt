package software.shonk.application.service

import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import software.shonk.domain.*
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.Settings
import software.shonk.interpreter.Shork

class ShorkServiceTest {

    @Test
    fun `inits with no default lobby`() {
        val shorkService = ShorkService(MockShork())
        assertEquals(0, shorkService.lobbies.size)
    }

    @Test
    fun `create lobby and playerA submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        val result = shorkService.getLobbyStatus(0L).getOrThrow()

        assertEquals(
            Status(
                playerASubmitted = true,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = GameResult(winner = Winner.DRAW),
                visualizationData = result.visualizationData,
            ),
            result,
        )
    }

    @Test
    fun `create lobby and playerB submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.addProgramToLobby(0L, "playerB", "someProgram")
        val result = shorkService.getLobbyStatus(0L).getOrThrow()

        assertEquals(
            Status(
                playerASubmitted = false,
                playerBSubmitted = true,
                gameState = GameState.NOT_STARTED,
                result = GameResult(winner = Winner.DRAW),
                visualizationData = result.visualizationData,
            ),
            result,
        )
    }

    @Test
    fun `create lobby creates a new lobby`() {
        val shorkService = ShorkService(MockShork())
        val lobbyId = shorkService.createLobby("playerA")
        assertEquals(1, shorkService.lobbies.size)

        val result = shorkService.getLobbyStatus(lobbyId.getOrThrow()).getOrThrow()
        assertEquals(
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = GameResult(winner = Winner.DRAW),
                visualizationData = result.visualizationData,
            ),
            result,
        )
    }

    @Test
    fun `upload code in lobby 0 does not affect lobby 1`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerB")
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        val result0 = shorkService.getLobbyStatus(0L).getOrThrow()
        assertEquals(
            result0,
            Status(
                playerASubmitted = true,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = GameResult(winner = Winner.DRAW),
                visualizationData = result0.visualizationData,
            ),
        )

        val result1 = shorkService.getLobbyStatus(1L).getOrThrow()
        assertEquals(
            result1,
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = GameResult(winner = Winner.DRAW),
                visualizationData = result1.visualizationData,
            ),
        )
    }

    @Test
    fun `add code with playerName null`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        val result = shorkService.addProgramToLobby(0L, null, "someProgram")

        assertEquals(true, result.isFailure)
        assertEquals("Invalid player name", result.exceptionOrNull()?.message)
    }

    @Test
    fun `delete lobby removes the lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.deleteLobby(0L)

        assertEquals(0, shorkService.lobbies.size)
    }

    @Test
    fun `delete lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.deleteLobby(0L)

        assertEquals(true, result.isFailure)
        assertEquals("No lobby with that id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `get status for the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.getLobbyStatus(0L)

        assertEquals(true, result.isFailure)
        assertEquals("No lobby with that id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `add program to the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.addProgramToLobby(0L, "playerA", "someProgram")

        assertEquals(true, result.isFailure)
        assertEquals("No lobby with that id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `set settings for the lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        val someSettings = Settings(69, 123, "NOP", 0)
        shorkService.setLobbySettings(0, someSettings)

        assertEquals(someSettings, shorkService.lobbies[0]?.getSettings())
    }

    @Test
    fun `set settings for invalid lobby`() {
        val shorkService = ShorkService(MockShork())
        val someSettings = Settings(69, 123, "NOP", 0)
        val result = shorkService.setLobbySettings(0, someSettings)

        assertEquals(true, result.isFailure)
        assertEquals("No lobby with that id", result.exceptionOrNull()?.message)
    }

    @Test
    fun `get code from lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
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
        shorkService.createLobby("playerA")
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        shorkService.addProgramToLobby(0L, "playerB", "someOtherProgram")

        val secondLobby = shorkService.createLobby("playerB")
        shorkService.addProgramToLobby(secondLobby.getOrThrow(), "playerA", "differentProgram")
        shorkService.addProgramToLobby(
            secondLobby.getOrThrow(),
            "playerB",
            "evenMoreDifferentProgram",
        )

        assertEquals("someProgram", shorkService.getProgramFromLobby(0L, "playerA").getOrNull())
        assertEquals(
            "someOtherProgram",
            shorkService.getProgramFromLobby(0L, "playerB").getOrNull(),
        )
        assertEquals(
            "differentProgram",
            shorkService.getProgramFromLobby(secondLobby.getOrThrow(), "playerA").getOrNull(),
        )
        assertEquals(
            "evenMoreDifferentProgram",
            shorkService.getProgramFromLobby(secondLobby.getOrThrow(), "playerB").getOrNull(),
        )
    }

    @Test
    fun `get code from lobby with invalid player`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
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
            shorkService.getProgramFromLobby(0L, "playerA").exceptionOrNull()?.message,
        )
    }

    @Test
    fun `join lobby with valid playerName`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.joinLobby(0L, "playerB")

        assertEquals(true, shorkService.lobbies[0]?.joinedPlayers?.contains("playerB"))
    }

    @Test
    fun `join lobby with duplicate (invalid) playerName`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.joinLobby(0L, "playerA")

        assertEquals(1, shorkService.lobbies[0]?.joinedPlayers?.size)
    }

    @Test
    fun `join nonexistent lobby`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.joinLobby(0L, "playerA")

        assertEquals(result.isFailure, true)
    }

    @Test
    fun `test get all lobbies`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerA")
        val result = shorkService.getAllLobbies()

        assertEquals(3, result.size)
        assertTrue(
            result.contains(
                LobbyStatus(id = 0L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )

        assertTrue(
            result.contains(
                LobbyStatus(id = 1L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )

        assertTrue(
            result.contains(
                LobbyStatus(id = 2L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )
    }

    @Test
    fun `get allLobbies but one is deleted`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerB")
        shorkService.deleteLobby(1L)

        val result = shorkService.getAllLobbies()

        assertEquals(3, result.size)
        assertTrue(
            result.contains(
                LobbyStatus(id = 0L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )

        assertTrue(
            result.contains(
                LobbyStatus(id = 2L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )

        assertTrue(
            result.contains(
                LobbyStatus(id = 3L, playersJoined = listOf("playerB"), gameState = "NOT_STARTED")
            )
        )
    }

    @Test
    fun `game visualization data exists`() {
        val shorkService = ShorkService(Shork())
        val lobbyId = shorkService.createLobby("playerA").getOrThrow()
        shorkService.addProgramToLobby(lobbyId, "playerA", "mov 0, 1")
        shorkService.addProgramToLobby(lobbyId, "playerB", "mov 0, 1")
        shorkService.setLobbySettings(lobbyId, Settings())

        val result = shorkService.getLobbyStatus(lobbyId).getOrThrow()
        assertEquals(GameState.FINISHED, result.gameState)
        assertEquals(Winner.DRAW, result.result.winner)
        assertTrue(result.visualizationData.isNotEmpty())
    }

    @Test
    fun `game visualization data absent before first run`() {
        val shorkService = ShorkService(Shork())
        val lobbyId = shorkService.createLobby("playerA").getOrThrow()
        shorkService.setLobbySettings(lobbyId, Settings())

        val result = shorkService.getLobbyStatus(lobbyId).getOrThrow()
        assertEquals(GameState.NOT_STARTED, result.gameState)
        assertEquals(Winner.DRAW, result.result.winner)
        assertTrue(result.visualizationData.isEmpty())
    }

    @Test
    fun `game visualization data can be disabled (not included)`() {
        val shorkService = ShorkService(Shork())
        val lobbyId = shorkService.createLobby("playerA").getOrThrow()
        shorkService.addProgramToLobby(lobbyId, "playerA", "mov 0, 1")
        shorkService.addProgramToLobby(lobbyId, "playerB", "jmp 42")

        val result =
            shorkService.getLobbyStatus(lobbyId, includeRoundInformation = false).getOrThrow()
        assertEquals(GameState.FINISHED, result.gameState)
        assertTrue(result.visualizationData.isEmpty())
    }

    @Test
    fun `test get lobby settings for a valid lobby`() {
        val shorkService = ShorkService(Shork())
        val lobbyId = shorkService.createLobby("playerA").getOrThrow()
        val defaultSettings = Settings()
        shorkService.setLobbySettings(lobbyId, defaultSettings)
        val result = shorkService.getLobbySettings(lobbyId)
        val testSettings = result.getOrThrow()

        assertTrue(
            testSettings ==
                InterpreterSettings(
                    coreSize = defaultSettings.coreSize,
                    instructionLimit = defaultSettings.instructionLimit,
                    initialInstruction = defaultSettings.initialInstruction,
                    maximumTicks = defaultSettings.maximumTicks,
                    maximumProcessesPerPlayer = defaultSettings.maximumProcessesPerPlayer,
                    readDistance = defaultSettings.readDistance,
                    writeDistance = defaultSettings.writeDistance,
                    minimumSeparation = defaultSettings.minimumSeparation,
                    separation = defaultSettings.separation,
                    randomSeparation = defaultSettings.randomSeparation,
                )
        )
    }

    @Test
    fun `test get lobby settings for an invalid lobby`() {
        val shorkService = ShorkService(Shork())
        val result = shorkService.getLobbySettings(999L)
        assertTrue(result.isFailure)
    }

    @Test
    fun `test authenticate joined players`() {
        val shorkService = ShorkService(Shork())
        val lobbyId = shorkService.createLobby("playerA").getOrThrow()
        val resultA = shorkService.authenticatePlayer("playerA", lobbyId)
        assertTrue(resultA.isSuccess)
        shorkService.joinLobby(lobbyId, "playerB")
        val resultB = shorkService.authenticatePlayer("playerB", lobbyId)
        assertTrue(resultB.isSuccess)
    }
}
