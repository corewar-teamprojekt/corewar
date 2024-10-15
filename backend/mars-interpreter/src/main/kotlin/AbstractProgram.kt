package software.shonk.interpreter

abstract class AbstractProgram {
    private val id: String
    private val processes: List<Process>

    constructor(id: String, sourceCode: String) {
        this.id = id
        this.processes = ArrayList()
    }
}
