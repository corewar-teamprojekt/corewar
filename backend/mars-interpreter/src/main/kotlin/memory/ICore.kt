package software.shonk.interpreter.memory

import software.shonk.interpreter.instruction.AbstractInstruction

interface ICore {
    fun load(index: Int): AbstractInstruction

    fun store(index: Int, instruction: AbstractInstruction)
}
