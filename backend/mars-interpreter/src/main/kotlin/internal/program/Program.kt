package software.shonk.interpreter.internal.program

import software.shonk.interpreter.internal.AbstractInternalShork
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.process.Process

internal class Program(id: String, shork: AbstractInternalShork) : AbstractProgram(id, shork) {

    override fun tick() {
        if (this.processes.isEmpty()) {
            return
        }
        this.processes.get().tick()
    }

    override fun createProcessAt(startingAddress: Int) {
        val newProcess = Process(this, startingAddress)
        this.processes.add(newProcess)
    }

    override fun removeProcess(process: AbstractProcess) {
        this.processes.removeByReference(process)
    }
}
