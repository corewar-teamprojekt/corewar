package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.compiler.Parser
import software.shonk.interpreter.internal.compiler.Token
import software.shonk.interpreter.internal.compiler.TokenType
import software.shonk.interpreter.internal.instruction.*
import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Mov
import software.shonk.interpreter.internal.instruction.Nop
import software.shonk.interpreter.internal.instruction.Seq
import software.shonk.interpreter.internal.instruction.Sne

internal class TestParser {
    @Test
    fun `CMP is alias for SEQ`() {
        val program =
            listOf(
                Token(TokenType.CMP, "CMP", "", 1),
                Token(TokenType.NUMBER, "42", 42, 1),
                Token(TokenType.COMMA, ",", "", 1),
                Token(TokenType.NUMBER, "1337", 1337, 1),
                Token(TokenType.I, "I", "", 1),
                Token(TokenType.EOF, "", "", 1),
            )

        val expected = listOf(Seq(42, 1337, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I))

        val parser = Parser(program)
        val instructions = parser.parse()

        assertEquals(expected, instructions)
    }

    @ParameterizedTest
    @MethodSource("provideAbsentModifierArguments")
    fun `test if absent modifiers are correctly filled in according to spec`(
        program: List<Token>,
        expected: List<AbstractInstruction>,
    ) {
        val parser = Parser(program)
        val instructions = parser.parse()

        if (parser.errors.isNotEmpty()) {
            println("Errors while parsing:")
            parser.errors.forEach { error(it) }
        }

        assertEquals(expected, instructions)
    }

    companion object {
        private val addressModes =
            listOf(
                Triple(AddressMode.DIRECT, "$", TokenType.DOLLAR),
                Triple(AddressMode.IMMEDIATE, "#", TokenType.HASHTAG),
                Triple(AddressMode.A_INDIRECT, "*", TokenType.STAR),
                Triple(AddressMode.B_INDIRECT, "@", TokenType.AT),
                Triple(AddressMode.A_PRE_DECREMENT, "{", TokenType.LEFT_BRACE),
                Triple(AddressMode.A_POST_INCREMENT, "}", TokenType.RIGHT_BRACE),
                Triple(AddressMode.B_PRE_DECREMENT, "<", TokenType.LOWER_THAN),
                Triple(AddressMode.B_POST_INCREMENT, ">", TokenType.GREATER_THAN),
            )

        @JvmStatic
        fun provideAbsentModifierArguments(): List<Arguments> {
            val arguments =
                listOf(
                    // MOV, SEQ and SNE get:
                    // - AB if A-Mode is IMMEDIATE
                    // - B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    // - I if neither is IMMEDIATE
                    // First the AB if A-Mode is IMMEDIATE is handled in
                    // generateAModeImmediateBModeAnyModifierAB

                    // Now B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.MOV, 42, 1337),
                        listOf(
                            Mov(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.SEQ, 42, 1337),
                        listOf(
                            Seq(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.SNE, 42, 1337),
                        listOf(
                            Sne(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.SEQ, 42, 1337),
                        listOf(
                            Seq(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.I,
                            )
                        ),
                    ),
                    // Now neither is IMMEDIATE
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.MOV, 42, 1337),
                        listOf(
                            Mov(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.I,
                            )
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.SNE, 42, 1337),
                        listOf(
                            Sne(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.I,
                            )
                        ),
                    ),

                    // ADD, SUB, MUL, DIV, MOD get:
                    // - AB if A-Mode is IMMEDIATE
                    // - B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    // - F if neither is IMMEDIATE
                    // First the AB if A-Mode is IMMEDIATE (handled via
                    // generateAModeImmediateBModeAnyModifierAB)

                    // Now B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.ADD, 42, 1337),
                        listOf(
                            Add(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.SUB, 42, 1337),
                        listOf(
                            Sub(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.MUL, 42, 1337),
                        listOf(
                            Mul(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.DIV, 42, 1337),
                        listOf(
                            Div(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        generateBImmediateAndNotAThenB(TokenType.MOD, 42, 1337),
                        listOf(
                            Mod(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),

                    // Now neither is IMMEDIATE
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.ADD, 42, 1337),
                        listOf(
                            Add(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.F,
                            )
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.SUB, 42, 1337),
                        listOf(
                            Sub(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.F,
                            )
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.MUL, 42, 1337),
                        listOf(
                            Mul(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.F,
                            )
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.DIV, 42, 1337),
                        listOf(
                            Div(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.F,
                            )
                        ),
                    ),
                    Arguments.of(
                        generateNeitherIsImmediate(TokenType.MOD, 42, 1337),
                        listOf(
                            Mod(
                                42,
                                1337,
                                AddressMode.A_INDIRECT,
                                AddressMode.A_INDIRECT,
                                Modifier.F,
                            )
                        ),
                    ),

                    // SLT, LDP, STP get:
                    // - AB if A-Mode is IMMEDIATE
                    // Always B otherwise
                    // AB if A-Mode is IMMEDIATE is handled via
                    // generateAModeImmediateBModeAnyModifierAB
                )

            return arguments +
                generateAlwaysModifierXXX() +
                generateAModeImmediateBModeAnyModifierAB()
        }

        private fun generateAModeImmediateBModeAnyModifierAB(): List<Arguments> {
            val firstAddress = 42
            val secondAddress = 1337
            val instructions =
                listOf(
                    Pair(TokenType.MOV, Mov::class),
                    Pair(TokenType.SEQ, Seq::class),
                    Pair(TokenType.SNE, Sne::class),
                    Pair(TokenType.ADD, Add::class),
                    Pair(TokenType.SUB, Sub::class),
                    Pair(TokenType.MUL, Mul::class),
                    Pair(TokenType.DIV, Div::class),
                    Pair(TokenType.MOD, Mod::class),
                    Pair(TokenType.SLT, Slt::class),
                    Pair(TokenType.LDP, Ldp::class),
                    Pair(TokenType.STP, Stp::class),
                )
            val arguments = mutableListOf<Arguments>()

            for (instruction in instructions) {
                val firstMode = Triple(AddressMode.IMMEDIATE, "#", TokenType.HASHTAG)
                for (secondMode in addressModes) {
                    val instance =
                        instruction.second.constructors
                            .first()
                            .call(
                                firstAddress,
                                secondAddress,
                                firstMode.first,
                                secondMode.first,
                                Modifier.AB,
                            )
                    arguments.add(
                        Arguments.of(
                            listOf(
                                Token(instruction.first, instruction.first.toString(), "", 1),
                                Token(firstMode.third, firstMode.second, "", 1),
                                Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1),
                                Token(TokenType.COMMA, ",", "", 1),
                                Token(secondMode.third, secondMode.second, "", 1),
                                Token(TokenType.NUMBER, secondAddress.toString(), secondAddress, 1),
                                Token(TokenType.EOF, "", "", 1),
                            ),
                            listOf(instance),
                        )
                    )
                }
            }

            return arguments
        }

        private fun generateAlwaysModifierXXX(): List<Arguments> {
            val firstAddress = 1337
            val secondAddress = 42
            val instructions =
                listOf(
                    // These are always F
                    Triple(TokenType.DAT, Dat::class, Modifier.F),
                    Triple(TokenType.NOP, Nop::class, Modifier.F),
                    // These are always B
                    Triple(TokenType.JMP, Jump::class, Modifier.B),
                    Triple(TokenType.JMZ, Jmz::class, Modifier.B),
                    Triple(TokenType.JMN, Jmn::class, Modifier.B),
                    Triple(TokenType.DJN, Djn::class, Modifier.B),
                    Triple(TokenType.SPL, Split::class, Modifier.B),
                )
            val arguments = mutableListOf<Arguments>()

            for (instruction in instructions) {
                for (firstMode in addressModes) {
                    for (secondMode in addressModes) {
                        val instance =
                            instruction.second.constructors
                                .first()
                                .call(
                                    firstAddress,
                                    secondAddress,
                                    firstMode.first,
                                    secondMode.first,
                                    instruction.third,
                                )
                        arguments.add(
                            Arguments.of(
                                listOf(
                                    Token(instruction.first, instruction.first.toString(), "", 1),
                                    Token(firstMode.third, firstMode.second, "", 1),
                                    Token(
                                        TokenType.NUMBER,
                                        firstAddress.toString(),
                                        firstAddress,
                                        1,
                                    ),
                                    Token(TokenType.COMMA, ",", "", 1),
                                    Token(secondMode.third, secondMode.second, "", 1),
                                    Token(
                                        TokenType.NUMBER,
                                        secondAddress.toString(),
                                        secondAddress,
                                        1,
                                    ),
                                    Token(TokenType.EOF, "", "", 1),
                                ),
                                listOf(instance),
                            )
                        )
                    }
                }
            }

            return arguments
        }

        private fun generateAImmediateThenAB(
            instructionToken: TokenType,
            firstAddress: Int,
            secondAddress: Int,
        ): List<Token> {
            return listOf(
                Token(instructionToken, instructionToken.toString(), "", 1),
                Token(TokenType.HASHTAG, "#", "", 1),
                Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1),
                Token(TokenType.COMMA, ",", "", 1),
                Token(TokenType.NUMBER, secondAddress.toString(), secondAddress, 1),
                Token(TokenType.EOF, "", "", 1),
            )
        }

        private fun generateBImmediateAndNotAThenB(
            instructionToken: TokenType,
            firstAddress: Int,
            secondAddress: Int,
        ): List<Token> {
            return listOf(
                Token(instructionToken, instructionToken.toString(), "", 1),
                Token(TokenType.STAR, "*", "", 1),
                Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1),
                Token(TokenType.COMMA, ",", "", 1),
                Token(TokenType.HASHTAG, "#", "", 1),
                Token(TokenType.NUMBER, secondAddress.toString(), secondAddress, 1),
                Token(TokenType.EOF, "", "", 1),
            )
        }

        private fun generateNeitherIsImmediate(
            instructionToken: TokenType,
            firstAddress: Int,
            secondAddress: Int,
        ): List<Token> {
            return listOf(
                Token(instructionToken, instructionToken.toString(), "", 1),
                Token(TokenType.STAR, "*", "", 1),
                Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1),
                Token(TokenType.COMMA, ",", "", 1),
                Token(TokenType.STAR, "*", "", 1),
                Token(TokenType.NUMBER, secondAddress.toString(), secondAddress, 1),
                Token(TokenType.EOF, "", "", 1),
            )
        }
    }
}
