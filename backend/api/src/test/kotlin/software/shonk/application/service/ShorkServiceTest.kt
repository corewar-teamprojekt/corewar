package software.shonk.application.service

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.domain.GameState
import software.shonk.domain.Result
import software.shonk.domain.Status
import software.shonk.domain.Winner
import software.shonk.interpreter.MockShork

class ShorkServiceTest {

    @Test
    fun `inits with default lobby with default status`() {
        val shorkService = ShorkService(MockShork())

        assertEquals(shorkService.lobbies.size, 1)
        assertEquals(shorkService.lobbies[0]?.id, 0)

        assertEquals(
            shorkService.getLobbyStatus(0L),
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }

    @Test
    fun `inits with default lobby and playerA submits program`() {
        val shorkService = ShorkService(MockShork())
        shorkService.addProgramToLobby(0L, "playerA", "someProgram")
        assertEquals(
            shorkService.getLobbyStatus(0L),
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
            shorkService.getLobbyStatus(0L),
            Status(
                playerASubmitted = false,
                playerBSubmitted = true,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            ),
        )
    }
}
