package software.shonk.interpreter.program

abstract class AbstractProgram(private val id: String) {

    // This function will be called once on every turn and should execute a single instruction
    // from one of the processes contained in the program
    abstract fun tick()

    fun getId(): String {
        return this.id
    }
}
