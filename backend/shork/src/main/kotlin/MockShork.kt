package software.shonk.interpreter

/** This is just a fake interpreter, that lets the longer program win */
class MockShork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): GameResult {
        val winner =
            programs
                .map { entry -> entry.key to entry.value.length }
                .maxByOrNull { it.second }
                ?.first

        return GameResult(GameOutcome(winner, OutcomeKind.WIN), emptyList())
    }
}
