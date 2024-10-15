package software.shonk.interpreter

interface ICore {
    fun load(index: Int): AbstractInstruction

    fun store(index: Int, instruction: AbstractInstruction)
}
