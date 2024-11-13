package software.shonk.interpreter

import software.shonk.interpreter.internal.statistics.RoundInformation

data class GameResult(val outcome: GameOutcome, val roundInformation: List<RoundInformation>)

data class GameOutcome(val player: String?, val kind: OutcomeKind)

enum class OutcomeKind {
    WIN,
    DRAW,
}
