package software.shonk.interpreter.internal.error

internal class TokenizerError
internal constructor(message: String, lineNumber: Int, charIndexStart: Int, charIndexEnd: Int) :
    AbstractCompilerError(message, lineNumber, charIndexStart, charIndexEnd)
