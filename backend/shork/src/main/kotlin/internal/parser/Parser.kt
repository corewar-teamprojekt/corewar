package software.shonk.interpreter.internal.parser

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.instruction.*
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.instruction.Compare
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Mov
import software.shonk.interpreter.internal.instruction.Split

internal class Parser(private val tokens: List<Token>) {
    private var current = 0
    private var instructions: MutableList<AbstractInstruction> = ArrayList()
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
                emitError("Unexpected token", token)
                advance()
            }
        }
    }

    private fun instruction(): AbstractInstruction? {
        val token = advance()
        var modifier = modifier()
        val (aField, modeA) = field()
        if (peek().type != TokenType.COMMA) {
            emitError("Expected comma after A Address but found ${peek().type}", token)
            advanceToNextInstruction()
            return null
        }
        advance() // Skip the comma
        val (bField, modeB) = field()

        // Handle if no modifier has been specified
        if (modifier == null) {
            when (token.type) {
                TokenType.DAT -> {
                    modifier = Modifier.F
                }
                TokenType.MOV,
                TokenType.CMP -> {
                    modifier =
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
                    modifier =
                        if (modeA == AddressMode.IMMEDIATE) {
                            Modifier.AB
                        } else if (modeB == AddressMode.IMMEDIATE) {
                            Modifier.B
                        } else {
                            Modifier.F
                        }
                }
                TokenType.SLT -> {
                    modifier =
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
                    modifier = Modifier.B
                }
                else -> {
                    if (isInstructionToken(token)) {
                        emitError("Default modifier handling not implemented for $token", token)
                    }
                    modifier = Modifier.I
                }
            }
        }

        return when (token.type) {
            TokenType.DAT -> Dat(aField, bField, modeA, modeB, modifier)
            TokenType.MOV -> Mov(aField, bField, modeA, modeB, modifier)
            TokenType.ADD -> {
                emitError("ADD has not been implemented yet", token)
                null
            }
            TokenType.SUB -> {
                emitError("SUB has not been implemented yet", token)
                null
            }
            TokenType.MUL -> {
                emitError("MUL has not been implemented yet", token)
                null
            }
            TokenType.DIV -> {
                emitError("DIV has not been implemented yet", token)
                null
            }
            TokenType.MOD -> {
                emitError("MOD has not been implemented yet", token)
                null
            }
            TokenType.JMP -> Jump(aField, bField, modeA, modeB, modifier)
            TokenType.JMZ -> {
                emitError("JMZ has not been implemented yet", token)
                null
            }
            TokenType.JMN -> {
                emitError("JMN has not been implemented yet", token)
                null
            }
            TokenType.DJN -> {
                emitError("DJN has not been implemented yet", token)
                null
            }
            TokenType.CMP -> Compare(aField, bField, modeA, modeB, modifier)
            TokenType.SLT -> {
                emitError("SLT has not been implemented yet", token)
                null
            }
            TokenType.SPL -> Split(aField, bField, modeA, modeB, modifier)
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
                emitError("Unexpected token, expected modifier after dot", token)
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
                        emitError("Unexpected token, expected address mode", token)
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
                TokenType.JMN,
                TokenType.DJN,
                TokenType.CMP,
                TokenType.SLT,
                TokenType.SPL,
                TokenType.ORG,
                TokenType.EQU,
                TokenType.END,
            )
    }

    private fun emitError(message: String, token: Token) {
        errors.add(Pair(message, token))
    }
}
