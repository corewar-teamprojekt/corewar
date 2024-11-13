package software.shonk.interpreter

import org.slf4j.LoggerFactory
import software.shonk.interpreter.internal.FinishedState
import software.shonk.interpreter.internal.GameStatus
import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.compiler.Parser
import software.shonk.interpreter.internal.compiler.Tokenizer
import software.shonk.interpreter.internal.program.Program

class Shork : IShork {
    private val logger = LoggerFactory.getLogger(Shork::class.java)

    override fun run(settings: Settings, programs: Map<String, String>): String {
        val internalSettings = settings.toInternalSettings()
        val shork = InternalShork(internalSettings)

        var lastLocation = 0
        for ((player, sourceCode) in programs.entries) {
            logger.info("Player $player:")
            val tokenizer = Tokenizer(sourceCode)
            val tokens = tokenizer.scanTokens()
            val parser = Parser(tokens)
            val instructions = parser.parse()

            logger.info("Successfully parsed ${instructions.size} instructions")
            if (tokenizer.tokenizingErrors.isNotEmpty()) {
                logger.error("Errors while tokenizing: ")
                tokenizer.tokenizingErrors.forEach { logger.error(it.toString()) }
            }

            if (parser.parsingErrors.isNotEmpty()) {
                logger.error("Errors while parsing: ")
                parser.parsingErrors.forEach { logger.error(it.toString()) }
            }

            if (instructions.size > internalSettings.instructionLimit) {
                logger.warn(
                    "Program of $player has exceeded the instruction limit of ${internalSettings.instructionLimit}"
                )
                continue
            }

            val program = Program(player, shork)
            shork.addProgram(program)

            val start = lastLocation
            logger.info("Storing the program of player $player at location $start")
            for (instruction in instructions) {
                shork.memoryCore.storeAbsolute(lastLocation++, instruction)
            }

            program.createProcessAt(start)
            lastLocation += internalSettings.minimumSeparation + 100
        }

        when (val result = shork.run()) {
            is GameStatus.FINISHED -> {
                return when (result.state) {
                    FinishedState.DRAW -> "DRAW"
                    is FinishedState.WINNER -> result.state.winner.playerId
                }
            }
            GameStatus.NOT_STARTED -> {
                throw IllegalStateException("Not started impossible after calling run")
            }
        }
    }
}
