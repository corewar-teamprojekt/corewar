package software.shonk.interpreter.internal.compiler

import java.util.*
import kotlin.collections.ArrayList

// https://en.wikipedia.org/wiki/Lexical_analysis#Tokenization
internal class Tokenizer(private val source: String) {
    private val tokens: MutableList<Token> = ArrayList()
    private var start = 0
    private var current = 0
    private var line = 1
    private val keywordMap: HashMap<String, TokenType> = HashMap<String, TokenType>()
    // Errors encountered while tokenizing, Pair of line number and error message
    val tokenizingErrors: MutableList<Pair<Int, String>> = ArrayList()

    init {
        // Instructions
        keywordMap["DAT"] = TokenType.DAT
        keywordMap["NOP"] = TokenType.NOP
        keywordMap["MOV"] = TokenType.MOV
        keywordMap["ADD"] = TokenType.ADD
        keywordMap["SUB"] = TokenType.SUB
        keywordMap["MUL"] = TokenType.MUL
        keywordMap["DIV"] = TokenType.DIV
        keywordMap["MOD"] = TokenType.MOD
        keywordMap["JMP"] = TokenType.JMP
        keywordMap["JMZ"] = TokenType.JMZ
        keywordMap["JMN"] = TokenType.JMN
        keywordMap["DJN"] = TokenType.DJN
        keywordMap["CMP"] = TokenType.CMP
        keywordMap["SLT"] = TokenType.SLT
        keywordMap["SPL"] = TokenType.SPL
        keywordMap["ORG"] = TokenType.ORG
        keywordMap["EQU"] = TokenType.EQU
        keywordMap["END"] = TokenType.END
        keywordMap["SEQ"] = TokenType.SEQ
        keywordMap["SNE"] = TokenType.SNE
        keywordMap["LDP"] = TokenType.LDP
        keywordMap["STP"] = TokenType.STP
        // Modifiers
        keywordMap["A"] = TokenType.A
        keywordMap["B"] = TokenType.B
        keywordMap["AB"] = TokenType.AB
        keywordMap["BA"] = TokenType.BA
        keywordMap["F"] = TokenType.F
        keywordMap["X"] = TokenType.X
        keywordMap["I"] = TokenType.I
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", "", line))
        return tokens
    }

    private fun scanToken() {
        when (val c: Char = advance()) {
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '*' -> addToken(TokenType.STAR)
            '/' -> addToken(TokenType.SLASH)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            ';' -> {
                addToken(TokenType.SEMICOLON)
                while (peek() != '\n' && !isAtEnd()) {
                    advance()
                }
            }
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            '<' -> addToken(TokenType.LOWER_THAN)
            '>' -> addToken(TokenType.GREATER_THAN)
            '#' -> addToken(TokenType.HASHTAG)
            '$' -> addToken(TokenType.DOLLAR)
            '@' -> addToken(TokenType.AT)
            ' ',
            '\r',
            '\t' -> {}
            '\n' -> {
                line++
            }
            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c.isLetter()) {
                    identifier()
                } else {
                    tokenizingErrors.add(Pair(line, "Unexpected character: ${c} at pos $current"))
                }
            }
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun peek(): Char {
        if (isAtEnd()) {
            return '\u0000'
        }
        return source[current]
    }

    private fun identifier() {
        while (peek().isLetterOrDigit()) {
            advance()
        }

        val text = source.substring(start, current).uppercase(Locale.getDefault())
        var type = TokenType.IDENTIFIER
        if (keywordMap.containsKey(text)) {
            type = keywordMap[text]!!
        }
        addToken(type)
    }

    private fun addToken(type: TokenType) {
        addToken(type, "")
    }

    private fun addToken(type: TokenType, literal: Any) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun number() {
        while (peek().isDigit()) {
            advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toLong())
    }
}
