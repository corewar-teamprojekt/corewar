package software.shonk.interpreter.process

import software.shonk.interpreter.program.AbstractProgram

// TODO: Needs an equals implementation. Maybe add an id field
abstract class AbstractProcess(val program: AbstractProgram) {
    var programCounter: Int = 0
}
