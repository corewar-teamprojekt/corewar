import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.shonk.interpreter.*

internal class TestShork {
    var settings = Settings()

    @BeforeEach
    fun setup() {
        settings = Settings()
    }

    @Test
    fun `test if one player can win`() {
        val shork = Shork()
        // Impy runs forever, blahaj jumps into a DAT, killing the program
        val programs = mapOf("impy" to "mov 0, 1", "blahaj" to "jmp $1, 0")
        val result = shork.run(settings, programs)

        assertEquals(GameOutcome("impy", OutcomeKind.WIN), result.getOrThrow().outcome)
    }

    @Test
    fun `test if draw happens with no programs`() {
        val shork = Shork()
        val programs = mapOf<String, String>()
        val result = shork.run(settings, programs)

        assertEquals(GameOutcome(null, OutcomeKind.DRAW), result.getOrThrow().outcome)
    }

    @Test
    fun `test if draw can happen with two programs that won't finish`() {
        val shork = Shork()
        // Both programs will jump in place
        val programs = mapOf("impy" to "jmp 0, 0", "blahaj" to "jmp 0, 0")
        val result = shork.run(settings, programs)

        assertEquals(GameOutcome(null, OutcomeKind.DRAW), result.getOrThrow().outcome)
    }

    @Test
    fun `test run fails if initial instruction is invalid`() {
        val settings = Settings(initialInstruction = "JMP.A $0 $0")
        val internalSettings = settings.toInternalSettings()
        assert(internalSettings.isFailure)
    }

    @Test
    fun `test integration of game data collection`() {
        val shork = Shork()
        val programs = mapOf("impy" to "mov 0, 1", "blahaj" to "jmp $1, 0")
        val result = shork.run(settings, programs)

        val roundInformation = result.getOrThrow().roundInformation
        assertEquals(6, roundInformation.size)
    }

    @Test
    fun `test integration of game data with more instructions per player`() {
        val shork = Shork()
        val programs =
            mapOf("impy" to "mov 0, 1\nmov 1, 2\nmov 2, 3", "blahaj" to "jmp $10, 0\n mov 2, 3")
        val result = shork.run(settings, programs)

        val roundInformation = result.getOrThrow().roundInformation
        assertEquals(6, roundInformation.size)
    }
}
