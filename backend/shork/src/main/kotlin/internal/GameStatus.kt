package software.shonk.interpreter.internal

import software.shonk.interpreter.internal.program.AbstractProgram

internal sealed class GameStatus {
    data object NOT_STARTED : GameStatus()

    data class FINISHED(val state: FinishedState) : GameStatus()
}

internal sealed class FinishedState {
    data class WINNER(val winner: AbstractProgram) : FinishedState()

    data object DRAW : FinishedState()
}
