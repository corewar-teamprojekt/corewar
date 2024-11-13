package software.shonk.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import software.shonk.interpreter.*

class LobbyTest {

    @Test
    fun canSetSetting() {
        val SOME_SETTINGS = Settings(69, 123, "NOP", 0)

        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = MockShork())

        lobby.setSettings(SOME_SETTINGS)
        assertEquals(SOME_SETTINGS, lobby.getSettings())
    }

    @Test
    fun initsWithDefaultStatus() {
        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = MockShork())
        val someStatus: Status =
            Status(
                playerASubmitted = false,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(Winner.UNDECIDED),
            )
        assertEquals(someStatus, lobby.getStatus())
    }

    @Test
    fun `player a submitted becomes true when the player uploads their program`() {
        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = MockShork())

        lobby.addProgram("playerA", "test")

        val someStatus: Status =
            Status(
                playerASubmitted = true,
                playerBSubmitted = false,
                gameState = GameState.NOT_STARTED,
                result = Result(Winner.UNDECIDED),
            )
        assertEquals(someStatus, lobby.getStatus())
    }

    @Test
    fun `player b submitted becomes true when the player uploads their program`() {
        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = MockShork())

        lobby.addProgram("playerB", "test")

        val someStatus: Status =
            Status(
                playerASubmitted = false,
                playerBSubmitted = true,
                gameState = GameState.NOT_STARTED,
                result = Result(Winner.UNDECIDED),
            )
        assertEquals(someStatus, lobby.getStatus())
    }

    @Test
    fun `runs game when both players have submitted their programs`() {
        val lobby = spyk(Lobby(id = 0, programs = HashMap<String, String>(), shork = MockShork()))

        lobby.addProgram("playerA", "someProgram")
        lobby.addProgram("playerB", "someOtherProgram")

        verify(exactly = 1) { lobby.run() }
    }

    @Test
    fun `check if game is finished and winner is decided`() {
        val shork = mockk<MockShork>()
        every { shork.run(any(), any()) } returns
            GameResult(GameOutcome("playerA", OutcomeKind.WIN), emptyList())

        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = shork)
        lobby.addProgram("playerA", "someProgram")
        lobby.addProgram("playerB", "someOtherProgram")

        val someStatus: Status =
            Status(
                playerASubmitted = true,
                playerBSubmitted = true,
                gameState = GameState.FINISHED,
                result = Result(Winner.A),
            )
        assertEquals(someStatus, lobby.getStatus())
    }

    @Test
    fun `tests if the shork is called upon during the lobby run`() {
        val shork = spyk<MockShork>()
        val lobby = Lobby(id = 0, programs = HashMap<String, String>(), shork = shork)
        lobby.addProgram("playerA", "someProgram")
        lobby.addProgram("playerB", "someOtherProgram")

        verify(exactly = 1) { shork.run(any(), any()) }
    }
}
