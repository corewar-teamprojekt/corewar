package software.shonk.interpreter.internal.error

abstract class AbstractCompilerError
internal constructor(
    val message: String,
    val lineNumber: Int,
    val charIndexStart: Int,
    val charIndexEnd: Int,
) {
    override fun toString(): String {
        return message
    }
}
