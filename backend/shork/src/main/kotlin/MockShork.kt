package software.shonk.interpreter

import software.shonk.interpreter.internal.statistics.RoundInformation

/** This is just a fake interpreter, that lets the longer program win */
class MockShork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): Result<GameResult> {
        val winner =
            programs
                .map { entry -> entry.key to entry.value.length }
                .maxByOrNull { it.second }
                ?.first

        return Result.success(
            GameResult(
                GameOutcome(winner, OutcomeKind.WIN),
                listOf(
                    RoundInformation(
                        playerId = "playerA",
                        programCounterBefore = 0,
                        programCounterAfter = 1,
                        listOf(1, 2, 3),
                        listOf(1, 2),
                        listOf(42, 69),
                        false,
                    )
                ),
            )
        )
    }
}
