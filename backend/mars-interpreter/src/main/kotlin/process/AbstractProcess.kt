package software.shonk.interpreter.process

import software.shonk.interpreter.program.AbstractProgram

abstract class AbstractProcess(val program: AbstractProgram) {
    var programCounter: Int = 0
}
