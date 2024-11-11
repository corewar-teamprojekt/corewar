package software.shonk.application.service

import io.mockk.spyk
import io.mockk.verify
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.Settings

class ShorkServiceTest {

    @Test
    fun `inits with no default lobby`() {
        val shorkService = ShorkService(MockShork())
        assertEquals(shorkService.lobbies.size, 0)
    }

    @Test
    fun `create lobby and playerA submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
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
    fun `create lobby and playerB submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
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
        shorkService.createLobby("playerA")
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
        val lobbyId = shorkService.createLobby("playerA")
        assertEquals(shorkService.lobbies.size, 1)
        assertEquals(
            shorkService.getLobbyStatus(lobbyId.getOrThrow()).getOrThrow(),
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
        shorkService.createLobby("playerA")
        shorkService.createLobby("playerB")
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
    fun `add code with playerName null`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        val result = shorkService.addProgramToLobby(0L, null, "someProgram")
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "Invalid player name")
    }

    @Test
    fun `delete lobby removes the lobby`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
        shorkService.deleteLobby(0L)
        assertEquals(shorkService.lobbies.size, 0)
    }

    @Test
    fun `delete lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.deleteLobby(0L)
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `get status for the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.getLobbyStatus(0L)
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
    }

    @Test
    fun `add program to the lobby fails if lobby does not exist`() {
        val shorkService = ShorkService(MockShork())
        val result = shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
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
        assertEquals(result.isFailure, true)
        assertEquals(result.exceptionOrNull()?.message, "No lobby with that id")
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
    fun `get code after game reset`() {
        val shorkService = ShorkService(MockShork())
        shorkService.createLobby("playerA")
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
    fun `check if a dead lobby gets closed after new code gets submitted by any player`() {
        val shorkService = spyk(ShorkService(MockShork()))
        shorkService.createLobby("playerA")
        val lobbyId = 0L
        shorkService.addProgramToLobby(lobbyId, "playerA", "someProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someOtherProgram")
        shorkService.addProgramToLobby(lobbyId, "playerB", "someNewOtherProgram")
        verify(exactly = 1) { shorkService.resetLobby(lobbyId) }
    }
}
