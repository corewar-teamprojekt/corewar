import kotlin.test.junit5.JUnit5Asserter.fail
import mocks.MockInstruction
import org.junit.jupiter.api.Assertions.assertEquals
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.memory.ICore
import software.shonk.interpreter.internal.settings.InternalSettings
import software.shonk.interpreter.internal.statistics.GameDataCollector
import software.shonk.interpreter.internal.statistics.IGameDataCollector

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

internal fun getDefaultInternalSettings(
    initialInstruction: AbstractInstruction,
    coreSize: Int = 8000,
    instructionLimit: Int = 1000,
    maximumCycles: Int = 1000,
    minimumSeparation: Int = 100,
    maximumProcessesPerPlayer: Int = 64,
    readDistance: Int = coreSize,
    writeDistance: Int = coreSize,
    separation: Int = 100,
    randomSeparation: Boolean = false,
    gameDataCollector: IGameDataCollector = GameDataCollector(),
): InternalSettings {
    return InternalSettings(
        coreSize,
        instructionLimit,
        initialInstruction,
        maximumCycles,
        maximumProcessesPerPlayer,
        readDistance,
        writeDistance,
        minimumSeparation,
        separation,
        randomSeparation,
        gameDataCollector,
    )
}
