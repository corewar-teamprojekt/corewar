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
        @JvmStatic
        fun provideAbsentModifierArguments(): List<Arguments> {
            val arguments =
                listOf(
                    // DAT and NOP always get the F modifier
                    Arguments.of(
                        listOf(
                            Token(TokenType.DAT, "DAT", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.NUMBER, "69", 69, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(Dat(42, 69, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)),
                    ),
                    Arguments.of(
                        listOf(
                            Token(TokenType.NOP, "NOP", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(Nop(42, 1337, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F)),
                    ),

                    // MOV, SEQ and SNE get:
                    // - AB if A-Mode is IMMEDIATE
                    // - B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    // - I if neither is IMMEDIATE
                    // First the AB if A-Mode is IMMEDIATE
                    Arguments.of(
                        listOf(
                            Token(TokenType.MOV, "MOV", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.NUMBER, "69", 69, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(Mov(42, 69, AddressMode.IMMEDIATE, AddressMode.DIRECT, Modifier.AB)),
                    ),
                    Arguments.of(
                        listOf(
                            Token(TokenType.SEQ, "SEQ", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(
                            Seq(42, 1337, AddressMode.IMMEDIATE, AddressMode.DIRECT, Modifier.AB)
                        ),
                    ),
                    Arguments.of(
                        listOf(
                            Token(TokenType.SNE, "SNE", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(
                            Sne(42, 1337, AddressMode.IMMEDIATE, AddressMode.DIRECT, Modifier.AB)
                        ),
                    ),
                    // Now B if B-Mode is IMMEDIATE and A-Mode is not IMMEDIATE
                    Arguments.of(
                        listOf(
                            Token(TokenType.MOV, "MOV", "", 1),
                            Token(TokenType.STAR, "*", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(
                            Mov(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        listOf(
                            Token(TokenType.SEQ, "SEQ", "", 1),
                            Token(TokenType.STAR, "*", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(
                            Seq(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                    ),
                    Arguments.of(
                        listOf(
                            Token(TokenType.SNE, "SNE", "", 1),
                            Token(TokenType.STAR, "*", "", 1),
                            Token(TokenType.NUMBER, "42", 42, 1),
                            Token(TokenType.COMMA, ",", "", 1),
                            Token(TokenType.HASHTAG, "#", "", 1),
                            Token(TokenType.NUMBER, "1337", 1337, 1),
                            Token(TokenType.EOF, "", "", 1),
                        ),
                        listOf(
                            Sne(42, 1337, AddressMode.A_INDIRECT, AddressMode.IMMEDIATE, Modifier.B)
                        ),
                        // Now neither is IMMEDIATE
                        Arguments.of(
                            listOf(
                                Token(TokenType.MOV, "MOV", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "42", 42, 1),
                                Token(TokenType.COMMA, ",", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "1337", 1337, 1),
                                Token(TokenType.EOF, "", "", 1),
                            ),
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
                            listOf(
                                Token(TokenType.SEQ, "SEQ", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "42", 42, 1),
                                Token(TokenType.COMMA, ",", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "1337", 1337, 1),
                                Token(TokenType.EOF, "", "", 1),
                            ),
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
                        Arguments.of(
                            listOf(
                                Token(TokenType.SNE, "SNE", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "42", 42, 1),
                                Token(TokenType.COMMA, ",", "", 1),
                                Token(TokenType.STAR, "*", "", 1),
                                Token(TokenType.NUMBER, "1337", 1337, 1),
                                Token(TokenType.EOF, "", "", 1),
                            ),
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
                    ),
                )

            return arguments
        }
    }
}
