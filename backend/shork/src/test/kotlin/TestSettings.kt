import kotlin.assert
import org.junit.jupiter.api.Test
import software.shonk.interpreter.Settings
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.Jmp

internal class TestSettings {

    @Test
    fun `test conversion to internal settings fails in initial instruction invalid`() {
        val settings = Settings(initialInstruction = "JMP.A $0 $0")
        val internalSettings = settings.toInternalSettings()
        assert(internalSettings.isFailure)
    }

    @Test
    fun `test conversion to internal settings successful in initial instruction valid`() {
        val settings = Settings(initialInstruction = "JMP.A $1, $2")
        val internalSettings = settings.toInternalSettings()
        assert(internalSettings.isSuccess)
        assert(
            internalSettings.getOrThrow().initialInstruction ==
                Jmp(1, 2, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.A)
        )
    }
}
