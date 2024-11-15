package software.shonk.interpreter.internal.compiler

import software.shonk.interpreter.internal.error.AbstractCompilerError
import software.shonk.interpreter.internal.instruction.AbstractInstruction

class Compiler(val sourceCode: String) {
    internal val tokens = mutableListOf<Token>()
    internal val instructions = mutableListOf<AbstractInstruction>()

    internal val tokenizer = Tokenizer(sourceCode)
    internal val tokenizerErrors = tokenizer.tokenizingErrors

    internal val parser = Parser(tokens)
    internal val parserErrors = parser.parsingErrors

    val allErrors: List<AbstractCompilerError>
        get() = parserErrors + tokenizerErrors

    val errorsOccured: Boolean
        get() = allErrors.isNotEmpty()

    init {
        compile()
    }

    private fun compile() {
        tokens.addAll(tokenizer.scanTokens())

        instructions.addAll(parser.parse())
    }
}
