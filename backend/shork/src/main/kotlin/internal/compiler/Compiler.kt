package software.shonk.interpreter.internal.compiler

import software.shonk.interpreter.internal.error.AbstractCompilerError
import software.shonk.interpreter.internal.error.ParserError
import software.shonk.interpreter.internal.error.TokenizerError
import software.shonk.interpreter.internal.instruction.AbstractInstruction

class Compiler(val sourceCode: String) {
    internal val tokens = mutableListOf<Token>()
    internal val instructions = mutableListOf<AbstractInstruction>()

    internal val parserErrors = mutableListOf<ParserError>()
    internal val tokenizerErrors = mutableListOf<TokenizerError>()
    val allErrors: List<AbstractCompilerError>
        get() = parserErrors + tokenizerErrors

    val errorsOccured: Boolean
        get() = allErrors.isNotEmpty()

    init {
        val tokenizer = Tokenizer(sourceCode)
        tokenizerErrors.addAll(tokenizer.tokenizingErrors)
        tokens.addAll(tokenizer.scanTokens())

        val parser = Parser(tokens)
        instructions.addAll(parser.parse())
        parserErrors.addAll(parser.parsingErrors)
    }
}
