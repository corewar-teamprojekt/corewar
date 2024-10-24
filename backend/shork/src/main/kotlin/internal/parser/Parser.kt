package software.shonk.interpreter.internal.parser

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.*
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.instruction.Compare
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Mov
import software.shonk.interpreter.internal.instruction.Split

internal class Parser(val tokens: List<Token>) {
    var current = 0
    var instructions: MutableList<AbstractInstruction> = ArrayList()
    var errors: MutableList<Pair<String, Token>> = ArrayList()

    fun parse(): List<AbstractInstruction> {
        while (!isAtEnd()) {
            parseInstruction()
        }

        return instructions
    }

    private fun isAtEnd(): Boolean {
        return current >= tokens.size
    }

    private fun peek(): Token {
        if (isAtEnd()) {
            return Token(TokenType.EOF, "", "", 0)
        }
        return tokens[current]
    }

    private fun advance(): Token {
        return tokens[current++]
    }

    private fun parseInstruction() {
        val token = peek()
        // We should always start with an instruction
        when (token.type) {
            TokenType.DAT,
            TokenType.MOV,
            TokenType.ADD,
            TokenType.SUB,
            TokenType.MUL,
            TokenType.DIV,
            TokenType.MOD,
            TokenType.JMP,
            TokenType.JMZ,
            TokenType.JMN,
            TokenType.DJN,
            TokenType.CMP,
            TokenType.SLT,
            TokenType.SPL,
            TokenType.ORG,
            TokenType.EQU,
            TokenType.END -> {
                val instruction = instruction()
                if (instruction != null) {
                    this.instructions.add(instruction)
                }
            }
            TokenType.EOF -> {
                advance()
            }
            else -> {
                errors.add(Pair("Unexpected token", token))
                advance()
            }
        }
    }

    private fun instruction(): AbstractInstruction? {
        val token = advance()
        val modifier = modifier() ?: return null
        val (aField, modeA) = field()
        if (peek().type != TokenType.COMMA) {
            errors.add(
                Pair(
                    "Expected comma after A Address but found ${peek().type} in line ${token.line}",
                    token,
                )
            )
            advanceToNextInstruction()
            return null
        }
        advance() // Skip the comma
        val (bField, modeB) = field()

        return when (token.type) {
            TokenType.DAT -> Dat(aField, bField, modeA, modeB, modifier)
            TokenType.MOV -> Mov(aField, bField, modeA, modeB, modifier)
            TokenType.ADD -> TODO()
            TokenType.SUB -> TODO()
            TokenType.MUL -> TODO()
            TokenType.DIV -> TODO()
            TokenType.MOD -> TODO()
            TokenType.JMP -> Jump(aField, bField, modeA, modeB, modifier)
            TokenType.JMZ -> TODO()
            TokenType.JMN -> TODO()
            TokenType.DJN -> TODO()
            TokenType.CMP -> Compare(aField, bField, modeA, modeB, modifier)
            TokenType.SLT -> TODO()
            TokenType.SPL -> Split(aField, bField, modeA, modeB, modifier)
            TokenType.ORG -> TODO()
            TokenType.EQU -> TODO()
            TokenType.END -> TODO()
            else -> {
                errors.add(Pair("Expected an instruction type", token))
                null
            }
        }
    }

    // Grab the modifier, if it exists
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
                errors.add(Pair("Unexpected token", token))
                null
            }
        }
    }

    private fun field(): Pair<Int, AddressMode> {
        var addressMode = AddressMode.DIRECT

        var token = advance()
        if (token.type != TokenType.NUMBER) {
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
                        errors.add(Pair("Unexpected token, expected address mode", token))
                        AddressMode.DIRECT
                    }
                }

            token = advance()
        }

        return when (token.type) {
            TokenType.NUMBER -> {
                var value = 0
                try {
                    value = token.lexeme.toInt()
                } catch (ex: NumberFormatException) {
                    errors.add(Pair("Couldn't parse as number: `${token.lexeme}`", token))
                }

                Pair(value, addressMode)
            }
            else -> {
                errors.add(Pair("Unexpected token, expected Field Value", token))
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
        return token.type in
            arrayOf(
                TokenType.DAT,
                TokenType.MOV,
                TokenType.ADD,
                TokenType.SUB,
                TokenType.MUL,
                TokenType.DIV,
                TokenType.MOD,
                TokenType.JMP,
                TokenType.JMZ,
                TokenType.DJN,
                TokenType.CMP,
                TokenType.SLT,
                TokenType.SPL,
                TokenType.ORG,
                TokenType.EQU,
                TokenType.END,
            )
    }
}
