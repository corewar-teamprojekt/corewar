package software.shonk.interpreter.internal.process

import software.shonk.interpreter.internal.program.AbstractProgram

internal class Process(program: AbstractProgram, programCounter: Int) :
    AbstractProcess(program, programCounter) {
    override fun tick() {
        val instruction = this.program.shork.memoryCore.loadAbsolute(programCounter)
        instruction.execute(this)
        if (!this.dontIncrementProgramCounter) {
            this.programCounter++
        }

        this.dontIncrementProgramCounter = false
    }
}
