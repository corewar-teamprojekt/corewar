package software.shonk.interpreter.internal.program

import software.shonk.interpreter.internal.AbstractInternalShork
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.process.Process

internal class Program(id: String, shork: AbstractInternalShork) : AbstractProgram(id, shork) {

    override fun tick() {
        if (this.processes.isEmpty()) {
            return
        }
        val gameDataCollector = this.shork.gameDataCollector
        val process = this.processes.get()

        gameDataCollector.collectProcessDataBeforeTick(process)

        process.tick()

        gameDataCollector.collectProcessDataAfterTick(process)
    }

    override fun createProcessAt(startingAddress: Int) {
        if (this.processes.size() >= this.shork.settings.maximumProcessesPerPlayer) {
            return
        }
        val newProcess = Process(this, startingAddress)
        this.processes.add(newProcess)
    }

    override fun removeProcess(process: AbstractProcess) {
        this.processes.removeByReference(process)
    }
}
