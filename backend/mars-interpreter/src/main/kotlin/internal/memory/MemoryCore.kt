package software.shonk.interpreter.internal.memory

import software.shonk.interpreter.internal.instruction.AbstractInstruction

internal class MemoryCore(memorySize: Int, defaultInstruction: AbstractInstruction) : ICore {
    private val memory: Array<AbstractInstruction> =
        Array(memorySize) { defaultInstruction.deepCopy() }

    override fun loadAbsolute(address: Int): AbstractInstruction {
        return memory[address % memory.size]
    }

    override fun storeAbsolute(address: Int, instruction: AbstractInstruction) {
        memory[address % memory.size] = instruction
    }
}
