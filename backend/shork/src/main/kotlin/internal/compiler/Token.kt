package software.shonk.interpreter.internal.compiler

internal class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any,
    val line: Int,
    val columnStart: Int,
    val columnEnd: Int,
) {
    override fun toString(): String {
        return "$type $lexeme $literal line: $line chars $columnStart-$columnEnd"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Token

        if (type != other.type) return false
        if (lexeme != other.lexeme) return false
        if (literal != other.literal) return false
        if (line != other.line) return false
        if (columnStart != other.columnStart) return false
        if (columnEnd != other.columnEnd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + lexeme.hashCode()
        result = 31 * result + literal.hashCode()
        result = 31 * result + line
        result = 31 * result + columnStart
        result = 31 * result + columnEnd
        return result
    }
}
