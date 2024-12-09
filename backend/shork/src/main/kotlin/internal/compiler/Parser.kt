package software.shonk.interpreter.internal.compiler

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.error.ParserError
import software.shonk.interpreter.internal.instruction.*
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Mov
import software.shonk.interpreter.internal.instruction.Spl

// https://en.wikipedia.org/wiki/Parsing#Parser
internal class Parser(private val tokens: List<Token>) {
    private var current = 0
    private var instructions: MutableList<AbstractInstruction> = ArrayList()
    var parsingErrors: MutableList<ParserError> = ArrayList()

    fun parse(): List<AbstractInstruction> {
        while (!isAtEnd()) {
            parseInstruction()
        }

        return instructions
    }

    private fun isAtEnd(): Boolean {
        return current >= (tokens.size - 1)
    }

    private fun peek(): Token {
        if (isAtEnd()) {
            return Token(TokenType.EOF, "", "", 0, 0, 0)
        }
        return tokens[current]
    }

    private fun advance(): Token {
        if (isAtEnd()) {
            return Token(TokenType.EOF, "", "", 0, 0, 0)
        }
        return tokens[current++]
    }

    private fun parseInstruction() {
        val token = peek()
        // We should always start with an instruction
        if (isInstructionToken(token)) {
            val instruction = instruction()
            if (instruction != null) {
                this.instructions.add(instruction)
            }
        } else {
            when (token.type) {
                TokenType.EOF,
                TokenType.SEMICOLON -> {
                    advance()
                }
                else -> {
                    emitError(
                        "Unexpected token, expected instruction, found '${token.lexeme}'",
                        token,
                    )
                    advance()
                }
            }
        }
    }

    private fun instruction(): AbstractInstruction? {
        val token = advance()
        var modifier = modifier()

        // Ensure the first field is specified
        var field = field()
        if (field == null) {
            if (isAtEnd()) {
                emitError(
                    "Unexpected end of file, expected address mode and/or address for A-Field",
                    token,
                )
            } else {
                emitError("Expected aField with modifier", peek())
            }
            return null
        }
        val (aField, modeA) = field

        if (peek().type == TokenType.COMMA) {
            advance() // Skip the comma
            field = field()
        } else {
            if (isInstructionToken(peek()) || isAtEnd()) {
                field = defaultField()
            } else {
                // We only allow the comma to be absent if the second field is missing
                val nextToken = peek()
                emitError("Expected comma after A Address but found ${nextToken.type}", nextToken)
                advanceToNextInstruction()
                return null
            }
        }

        // Allow the second field to be missing and fill in a default
        val (bField, modeB) = field ?: defaultField()

        // Handle if no modifier has been specified
        if (modifier == null) {
            modifier = getDefaultModifier(token, modeA, modeB)
        }

        return when (token.type) {
            TokenType.DAT -> Dat(aField, bField, modeA, modeB, modifier)
            TokenType.NOP -> Nop(aField, bField, modeA, modeB, modifier)
            TokenType.MOV -> Mov(aField, bField, modeA, modeB, modifier)
            TokenType.ADD -> Add(aField, bField, modeA, modeB, modifier)
            TokenType.SUB -> Sub(aField, bField, modeA, modeB, modifier)
            TokenType.MUL -> Mul(aField, bField, modeA, modeB, modifier)
            TokenType.DIV -> Div(aField, bField, modeA, modeB, modifier)
            TokenType.MOD -> Mod(aField, bField, modeA, modeB, modifier)
            TokenType.JMP -> Jmp(aField, bField, modeA, modeB, modifier)
            TokenType.JMZ -> Jmz(aField, bField, modeA, modeB, modifier)
            TokenType.JMN -> Jmn(aField, bField, modeA, modeB, modifier)
            TokenType.DJN -> Djn(aField, bField, modeA, modeB, modifier)
            TokenType.CMP -> Seq(aField, bField, modeA, modeB, modifier)
            TokenType.SEQ -> Seq(aField, bField, modeA, modeB, modifier)
            TokenType.SNE -> Sne(aField, bField, modeA, modeB, modifier)
            TokenType.SLT -> Slt(aField, bField, modeA, modeB, modifier)
            TokenType.SPL -> Spl(aField, bField, modeA, modeB, modifier)
            TokenType.STP -> Stp(aField, bField, modeA, modeB, modifier)
            TokenType.LDP -> Ldp(aField, bField, modeA, modeB, modifier)
            TokenType.ORG -> {
                emitError("ORG has not been implemented yet", token)
                null
            }
            TokenType.EQU -> {
                emitError("EQU has not been implemented yet", token)
                null
            }
            TokenType.END -> {
                emitError("END has not been implemented yet", token)
                null
            }
            else -> {
                emitError("Unexpected token, expected an instruction", token)
                null
            }
        }
    }

    private fun getDefaultModifier(token: Token, modeA: AddressMode, modeB: AddressMode): Modifier {
        return when (token.type) {
            TokenType.DAT,
            TokenType.NOP -> {
                Modifier.F
            }
            TokenType.MOV,
            TokenType.CMP,
            TokenType.SEQ,
            TokenType.SNE -> {
                if (modeA == AddressMode.IMMEDIATE) {
                    Modifier.AB
                } else if (modeB == AddressMode.IMMEDIATE) {
                    Modifier.B
                } else {
                    Modifier.I
                }
            }
            TokenType.ADD,
            TokenType.SUB,
            TokenType.MUL,
            TokenType.DIV,
            TokenType.MOD -> {
                if (modeA == AddressMode.IMMEDIATE) {
                    Modifier.AB
                } else if (modeB == AddressMode.IMMEDIATE) {
                    Modifier.B
                } else {
                    Modifier.F
                }
            }
            TokenType.SLT,
            TokenType.LDP,
            TokenType.STP -> {
                if (modeA == AddressMode.IMMEDIATE) {
                    Modifier.AB
                } else {
                    Modifier.B
                }
            }
            TokenType.JMP,
            TokenType.JMZ,
            TokenType.JMN,
            TokenType.DJN,
            TokenType.SPL -> {
                Modifier.B
            }
            else -> {
                if (isInstructionToken(token)) {
                    emitError("Default modifier handling not implemented for $token", token)
                }
                Modifier.I
            }
        }
    }

    /** Parses the modifier of an instruction, if it exists */
    private fun modifier(): Modifier? {
        if (peek().type != TokenType.DOT) {
            return null
        }

        // next token is DOT, so we skip it
        advance()

        val token = advance()
        return when (token.type) {
            TokenType.A -> Modifier.A
            TokenType.B -> Modifier.B
            TokenType.AB -> Modifier.AB
            TokenType.BA -> Modifier.BA
            TokenType.F -> Modifier.F
            TokenType.X -> Modifier.X
            TokenType.I -> Modifier.I
            else -> {
                emitError("Unexpected token, expected modifier after dot", token)
                null
            }
        }
    }

    private fun defaultField(): Pair<Int, AddressMode> {
        return Pair(0, AddressMode.DIRECT)
    }

    private fun field(): Pair<Int, AddressMode>? {
        var addressMode = AddressMode.DIRECT

        if (isAtEnd()) {
            return null
        }

        peek().let {
            if (
                it.type != TokenType.NUMBER && !isAddressModeToken(it) && it.type != TokenType.MINUS
            ) {
                return null
            }
        }

        var token = advance()
        if (token.type != TokenType.NUMBER && token.type != TokenType.MINUS) {
            addressMode =
                when (token.type) {
                    TokenType.HASHTAG -> AddressMode.IMMEDIATE
                    TokenType.DOLLAR -> AddressMode.DIRECT
                    TokenType.STAR -> AddressMode.A_INDIRECT
                    TokenType.AT -> AddressMode.B_INDIRECT
                    TokenType.LEFT_BRACE -> AddressMode.A_PRE_DECREMENT
                    TokenType.RIGHT_BRACE -> AddressMode.A_POST_INCREMENT
                    TokenType.LOWER_THAN -> AddressMode.B_PRE_DECREMENT
                    TokenType.GREATER_THAN -> AddressMode.B_POST_INCREMENT
                    else -> {
                        emitError("Unexpected token, expected address mode", token)
                        AddressMode.DIRECT
                    }
                }

            token = advance()
        }

        return when (token.type) {
            TokenType.MINUS -> {
                var value = 0
                if (peek().type == TokenType.NUMBER) {
                    token = advance()
                    try {
                        value = -1 * token.lexeme.toInt()
                    } catch (ex: NumberFormatException) {
                        emitError("Couldn't parse as number: `${token.lexeme}`", token)
                    }
                }

                Pair(value, addressMode)
            }
            TokenType.NUMBER -> {
                var value = 0
                try {
                    value = token.lexeme.toInt()
                } catch (ex: NumberFormatException) {
                    emitError("Couldn't parse as number: `${token.lexeme}`", token)
                }

                Pair(value, addressMode)
            }
            else -> {
                emitError("Unexpected token, expected Field Value", token)
                Pair(0, AddressMode.IMMEDIATE)
            }
        }
    }

    private fun advanceToNextInstruction() {
        while (!isInstructionToken(peek()) && !isAtEnd()) {
            advance()
        }
    }

    private fun isInstructionToken(token: Token): Boolean {
        return token.type in TokenType.instructions()
    }

    private fun isModifierToken(token: Token): Boolean {
        return token.type in TokenType.modifiers()
    }

    private fun isAddressModeToken(token: Token): Boolean {
        return token.type in TokenType.addressModes()
    }

    private fun emitError(
        message: String,
        token: Token,
        line: Int,
        columnStart: Int,
        columnEnd: Int,
    ) {
        parsingErrors.add(ParserError(message, line, columnStart, columnEnd, token))
    }

    private fun emitError(message: String, token: Token) {
        parsingErrors.add(
            ParserError(message, token.line, token.columnStart, token.columnEnd, token)
        )
    }
}
