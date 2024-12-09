package software.shonk.interpreter

import org.slf4j.LoggerFactory
import software.shonk.interpreter.internal.FinishedState
import software.shonk.interpreter.internal.GameStatus
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.compiler.Compiler
import software.shonk.interpreter.internal.program.Program

class Shork : IShork {
    private val logger = LoggerFactory.getLogger(Shork::class.java)

    override fun run(settings: Settings, programs: Map<String, String>): Result<GameResult> {
        val internalSettings =
            settings
                .toInternalSettings()
                .getOrElse({
                    return Result.failure(it)
                })

        val shork = InternalShork(internalSettings)

        var address = -1 * settings.separation

        for ((player, sourceCode) in programs.entries) {
            logger.info("Player $player:")
            val compiler = Compiler(sourceCode)
            val instructions = compiler.instructions
            val tokenizingErrors = compiler.tokenizerErrors
            val parsingErrors = compiler.parserErrors

            logger.info("Successfully parsed ${instructions.size} instructions")
            if (tokenizingErrors.isNotEmpty()) {
                logger.error("Errors while tokenizing: ")
                tokenizingErrors.forEach { logger.error(it.toString()) }
            }

            if (parsingErrors.isNotEmpty()) {
                logger.error("Errors while parsing: ")
                parsingErrors.forEach { logger.error(it.toString()) }
            }

            if (instructions.size > internalSettings.instructionLimit) {
                logger.warn(
                    "Program of $player has exceeded the instruction limit of ${internalSettings.instructionLimit}"
                )
                continue
            }

            val program = Program(player, shork)
            shork.addProgram(program)
            shork.gameDataCollector.startRoundForProgram(program)

            address =
                if (internalSettings.randomSeparation) {
                    (0 until internalSettings.coreSize).random()
                    // @TODO: Test once functionality is inside InternalShork
                } else {
                    address + internalSettings.separation
                }

            val start = address
            logger.info("Storing the program of player $player at location $start")
            for (instruction in instructions) {
                shork.memoryCore.storeAbsolute(address++, instruction)
            }
            program.createProcessAt(start)
            shork.gameDataCollector.endRoundForProgram(program)
        }

        val outcome =
            when (val result = shork.run()) {
                is GameStatus.FINISHED -> {
                    when (result.state) {
                        FinishedState.DRAW -> GameOutcome(null, OutcomeKind.DRAW)
                        is FinishedState.WINNER ->
                            GameOutcome(result.state.winner.playerId, OutcomeKind.WIN)
                    }
                }
                GameStatus.NOT_STARTED -> {
                    throw IllegalStateException("Not started impossible after calling run")
                }
            }

        return Result.success(
            GameResult(
                outcome = outcome,
                roundInformation = shork.gameDataCollector.getGameStatistics(),
            )
        )
    }
}
