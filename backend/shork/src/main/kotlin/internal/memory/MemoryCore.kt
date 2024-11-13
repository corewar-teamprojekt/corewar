package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.statistics.IGameDataCollector

internal class MemoryCore(
    memorySize: Int,
    defaultInstruction: AbstractInstruction,
    val gameDataCollector: IGameDataCollector,
) : ICore {
    private val memory: Array<AbstractInstruction> =
        Array(memorySize) { defaultInstruction.deepCopy() }

    override fun loadAbsolute(address: Int): AbstractInstruction {
        val resolvedAddress = address % memory.size
        gameDataCollector.collectMemoryRead(resolvedAddress)
        return memory[resolvedAddress]
    }

    override fun storeAbsolute(address: Int, instruction: AbstractInstruction) {
        val resolvedAddress = address % memory.size
        gameDataCollector.collectMemoryWrite(resolvedAddress, instruction)
        memory[resolvedAddress] = instruction
    }
}
