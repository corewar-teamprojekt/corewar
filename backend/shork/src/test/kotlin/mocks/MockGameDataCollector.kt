package mocks

import org.slf4j.LoggerFactory
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.program.AbstractProgram
import software.shonk.interpreter.internal.statistics.IGameDataCollector
import software.shonk.interpreter.internal.statistics.RoundInformation

internal class MockGameDataCollector : IGameDataCollector {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun startRoundForProgram(program: AbstractProgram) {}

    override fun endRoundForProgram(program: AbstractProgram) {}

    override fun collectProcessDataBeforeTick(process: AbstractProcess) {}

    override fun collectProcessDataAfterTick(process: AbstractProcess) {}

    override fun collectMemoryRead(absoluteAddress: Int) {}

    override fun collectMemoryWrite(absoluteAddress: Int, instruction: AbstractInstruction) {}

    override fun getGameStatistics(): List<RoundInformation> {
        return emptyList()
    }
}
