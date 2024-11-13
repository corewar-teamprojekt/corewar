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

        assertEquals(GameOutcome("impy", OutcomeKind.WIN), result.outcome)
    }

    @Test
    fun `test if draw happens with no programs`() {
        val shork = Shork()
        val programs = mapOf<String, String>()
        val result = shork.run(settings, programs)

        assertEquals(GameOutcome(null, OutcomeKind.DRAW), result.outcome)
    }

    @Test
    fun `test if draw can happen with two programs that won't finish`() {
        val shork = Shork()
        // Both programs will jump in place
        val programs = mapOf("impy" to "jmp 0, 0", "blahaj" to "jmp 0, 0")
        val result = shork.run(settings, programs)

        assertEquals(GameOutcome(null, OutcomeKind.DRAW), result.outcome)
    }
}
