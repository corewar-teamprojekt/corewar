package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.settings.InternalSettings

internal class MemoryCore(memorySize: Int, val settings: InternalSettings) : ICore {
    private val memory: Array<AbstractInstruction> =
        Array(memorySize) { settings.initialInstruction.deepCopy() }

    override fun loadAbsolute(address: Int): AbstractInstruction {
        val resolvedAddress = address % memory.size
        settings.gameDataCollector.collectMemoryRead(resolvedAddress)
        return memory[resolvedAddress]
    }

    override fun storeAbsolute(address: Int, instruction: AbstractInstruction) {
        val resolvedAddress = address % memory.size
        settings.gameDataCollector.collectMemoryWrite(resolvedAddress, instruction)
        memory[resolvedAddress] = instruction
    }
}
