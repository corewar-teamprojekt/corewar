package software.shonk.interpreter

import software.shonk.interpreter.program.AbstractProgram

// TODO: Needs to be implemented
class Program : AbstractProgram {
    private val processes: List<Process>
    private var processCounter = 0

    constructor(id: String, sourceCode: String) : super(id) {
        this.processes = ArrayList()
        // TODO: Translate Source Code to Process Instance
    }

    override fun tick() {
        TODO("Not yet implemented")
    }
}
