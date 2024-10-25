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

class ShorkServiceTest {

    @Test
    fun initsWithSaneDefaultStatus() {
        val shorkService = ShorkService(MockShork())

        val expected =
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(winner = Winner.UNDECIDED),
            )

        assertEquals(expected, shorkService.getStatus())
    }

    @Test
    fun settingPlayerAProgramUpdatesStatus() {
        val shorkService = ShorkService(MockShork())

        assertEquals(false, shorkService.getStatus().playerASubmitted)
        shorkService.addProgram("playerA", "programcode")
        assertEquals(true, shorkService.getStatus().playerASubmitted)
    }

    @Test
    fun settingPlayerBProgramUpdatesStatus() {
        val shorkService = ShorkService(MockShork())

        assertEquals(false, shorkService.getStatus().playerBSubmitted)
        shorkService.addProgram("playerB", "other program code")
        assertEquals(true, shorkService.getStatus().playerBSubmitted)
    }

    @Test
    fun uploadingPlayerAAndBRunsGame() {
        val shorkService = ShorkService(MockShork())

        shorkService.addProgram("playerA", "programcode")
        shorkService.addProgram("playerB", "other program code")

        val status = shorkService.getStatus()
        assertEquals(GameState.FINISHED, status.gameState)
    }

    @Test
    fun runningGameCallsShork() {
        val shork = spyk<MockShork>()
        val shorkService = ShorkService(shork)

        shorkService.addProgram("playerA", "programcode")
        shorkService.addProgram("playerB", "other program code")

        verify(exactly = 1) { shork.run(any(), any()) }
    }
}
