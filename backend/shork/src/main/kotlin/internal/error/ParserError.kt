package software.shonk.interpreter.internal.error

import software.shonk.interpreter.internal.compiler.Token

class ParserError
internal constructor(
    message: String,
    line: Int,
    columnStart: Int,
    columnEnd: Int,
    internal val token: Token?,
) : AbstractCompilerError(message, line, columnStart, columnEnd) {
    override fun toString(): String {
        return "ParserError: $message at line $lineNumber, chars $lineCharIndexStart-$lineCharIndexEnd"
    }
}
