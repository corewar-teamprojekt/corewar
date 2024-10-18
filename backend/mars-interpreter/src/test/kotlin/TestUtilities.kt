import kotlin.test.junit5.JUnit5Asserter.fail
import mocks.MockInstruction
import org.junit.jupiter.api.Assertions.assertEquals
import software.shonk.interpreter.internal.memory.ICore

internal fun assertExecutionCountAtAddress(
    memoryCore: ICore,
    address: Int,
    expectedExecutionCount: Int,
) {
    val instruction = memoryCore.loadAbsolute(address)
    if (instruction is MockInstruction) {
        assertEquals(expectedExecutionCount, instruction.executionCount)
    } else {
        fail(
            "Expected instruction at address $address to be a MockInstruction, but was $instruction"
        )
    }
}
