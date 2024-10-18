import kotlin.test.assertEquals
import mocks.KillProgramInstruction
import mocks.MockInstruction
import org.junit.jupiter.api.Test
import software.shonk.interpreter.FinishedState
import software.shonk.interpreter.GameStatus
import software.shonk.interpreter.InternalShork
import software.shonk.interpreter.internal.program.Program
import software.shonk.interpreter.internal.settings.InternalSettings

internal class TestInternalShork {
    @Test
    fun gameStatusIsDrawWithNoProgramsAndRunCalled() {
        val shork = InternalShork(InternalSettings(8000, 100, MockInstruction(), 1000))
        val status: GameStatus = shork.run()

        assertEquals(GameStatus.FINISHED(FinishedState.DRAW), status)
    }

    @Test
    fun gameStatusIsPlayerWon() {
        val shork = InternalShork(InternalSettings(8000, 100, KillProgramInstruction(), 1000))
        val program = Program("Blahaj \uD83E\uDD7A", shork)
        val program2 = Program("Shork \uD83E\uDD88", shork)
        program2.createProcessAt(1337)
        shork.addProgram(program, program2)

        val status: GameStatus = shork.run()

        // We used the KillProgramInstruction, so the program that was created first should lose as
        // it gets ticked first.
        assertEquals(GameStatus.FINISHED(FinishedState.WINNER(program2)), status)
    }
}
