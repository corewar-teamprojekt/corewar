package software.shonk.interpreter.program

import software.shonk.interpreter.IShork

// TODO: Needs to be implemented
class Program : AbstractProgram {
    private val processes: List<Process>
    private var processCounter = 0

    constructor(id: String, sourceCode: String, shork: IShork) : super(id, shork) {
        this.processes = ArrayList()
        // TODO: Translate Source Code to Process Instance
    }

    override fun tick() {
        TODO("Not yet implemented")
    }
}
