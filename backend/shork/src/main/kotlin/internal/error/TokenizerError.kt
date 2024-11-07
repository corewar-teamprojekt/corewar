package software.shonk.interpreter.internal.error

class TokenizerError
internal constructor(message: String, lineNumber: Int, charIndexStart: Int, charIndexEnd: Int) :
    AbstractCompilerError(message, lineNumber, charIndexStart, charIndexEnd)
