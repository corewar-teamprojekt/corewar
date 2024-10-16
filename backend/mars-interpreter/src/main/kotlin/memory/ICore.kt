package software.shonk.interpreter.memory

import software.shonk.interpreter.instruction.AbstractInstruction

interface ICore {
    fun loadAbsolute(index: Int): AbstractInstruction

    fun storeAbsolute(index: Int, instruction: AbstractInstruction)
}
