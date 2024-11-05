package software.shonk.interpreter

import software.shonk.interpreter.internal.InternalShork
import software.shonk.interpreter.internal.compiler.Parser
import software.shonk.interpreter.internal.compiler.Tokenizer
import software.shonk.interpreter.internal.program.Program

class Shork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): String {
        val internalSettings = settings.toInternalSettings()
        val shork = InternalShork(internalSettings)

        var lastLocation = 0
        for ((player, sourceCode) in programs.entries) {
            println("Player $player:")
            val tokenizer = Tokenizer(sourceCode)
            val tokens = tokenizer.scanTokens()
            val parser = Parser(tokens)
            val instructions = parser.parse()

            println("Successfully parsed ${instructions.size} instructions")
            println("Errors while tokenizing: ")
            tokenizer.tokenizingErrors.forEach { println(it) }
            println("Errors while parsing: ")
            parser.parsingErrors.forEach { println(it) }

            if (instructions.size > internalSettings.instructionLimit) {
                println(
                    "Program of $player has exceeded the instruction limit of ${internalSettings.instructionLimit}"
                )
                continue
            }

            val program = Program(player, shork)
            shork.addProgram(program)

            val start = lastLocation
            println("Storing the program of player $player at location $start")
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
                    is FinishedState.WINNER -> result.state.winner.id
                }
            }
            GameStatus.NOT_STARTED -> {
                throw IllegalStateException("Not started impossible after calling run")
            }
        }
    }
}
