package software.shonk.interpreter.internal.error

import software.shonk.interpreter.internal.compiler.Token

internal class ParserError
internal constructor(
    message: String,
    line: Int,
    columnStart: Int,
    columnEnd: Int,
    internal val token: Token?,
) : AbstractCompilerError(message, line, columnStart, columnEnd)
