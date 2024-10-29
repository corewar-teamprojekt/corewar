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
            return `generate modifier is always XXX`() +
                `generate A-Mode is immediate, B-Mode is any then modifier is AB`() +
                `generate modifier is B if B-Mode is immediate and A-Mode isn't`() +
                `generate modifier is always B if A-Mode is not immediate`() +
                `generate neither mode is immediate`()
        }

        private fun `generate neither mode is immediate`(): List<Arguments> {
            val firstAddress = 42
            val secondAddress = 1337
            val instructions =
                listOf(
                    // These are always I
                    Triple(TokenType.MOV, Mov::class, Modifier.I),
                    Triple(TokenType.SEQ, Seq::class, Modifier.I),
                    Triple(TokenType.SNE, Sne::class, Modifier.I),
                    Triple(TokenType.CMP, Seq::class, Modifier.I),
                    // The arithmetic instructions are always F
                    Triple(TokenType.ADD, Add::class, Modifier.F),
                    Triple(TokenType.SUB, Sub::class, Modifier.F),
                    Triple(TokenType.MUL, Mul::class, Modifier.F),
                    Triple(TokenType.DIV, Div::class, Modifier.F),
                    Triple(TokenType.MOD, Mod::class, Modifier.F),
                )

            val arguments = mutableListOf<Arguments>()
            val addressModesWithoutImmediate =
                this.addressModes.filter { it.first != AddressMode.IMMEDIATE }

            for (instruction in instructions) {
                for (firstMode in addressModesWithoutImmediate) {
                    for (secondMode in addressModesWithoutImmediate) {
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

        private fun `generate modifier is B if B-Mode is immediate and A-Mode isn't`():
            List<Arguments> {
            val firstAddress = 42
            val secondAddress = 1337
            val instructions =
                listOf(
                    Pair(TokenType.MOV, Mov::class),
                    Pair(TokenType.SEQ, Seq::class),
                    Pair(TokenType.SNE, Sne::class),
                    Pair(TokenType.CMP, Seq::class),
                    Pair(TokenType.ADD, Add::class),
                    Pair(TokenType.SUB, Sub::class),
                    Pair(TokenType.MUL, Mul::class),
                    Pair(TokenType.DIV, Div::class),
                    Pair(TokenType.MOD, Mod::class),
                )

            val arguments = mutableListOf<Arguments>()
            val addressModesWithoutImmediate =
                this.addressModes.filter { it.first != AddressMode.IMMEDIATE }

            for (instruction in instructions) {
                for (firstAddressMode in addressModesWithoutImmediate) {
                    val secondAddressMode = Triple(AddressMode.IMMEDIATE, "#", TokenType.HASHTAG)
                    val instance =
                        instruction.second.constructors
                            .first()
                            .call(
                                firstAddress,
                                secondAddress,
                                firstAddressMode.first,
                                secondAddressMode.first,
                                Modifier.B,
                            )
                    arguments.add(
                        Arguments.of(
                            listOf(
                                Token(instruction.first, instruction.first.toString(), "", 1),
                                Token(firstAddressMode.third, firstAddressMode.second, "", 1),
                                Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1),
                                Token(TokenType.COMMA, ",", "", 1),
                                Token(secondAddressMode.third, secondAddressMode.second, "", 1),
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

        private fun `generate modifier is always B if A-Mode is not immediate`(): List<Arguments> {
            val firstAddress = 42
            val secondAddress = 1337
            val instructions =
                listOf(
                    Pair(TokenType.SLT, Slt::class),
                    Pair(TokenType.LDP, Ldp::class),
                    Pair(TokenType.STP, Stp::class),
                )
            val arguments = mutableListOf<Arguments>()
            val addressModesWithoutImmediate =
                this.addressModes.filter { it.first != AddressMode.IMMEDIATE }

            for (instruction in instructions) {
                for (firstMode in addressModesWithoutImmediate) {
                    for (secondMode in this.addressModes) {
                        val instance =
                            instruction.second.constructors
                                .first()
                                .call(
                                    firstAddress,
                                    secondAddress,
                                    firstMode.first,
                                    secondMode.first,
                                    Modifier.B,
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

        private fun `generate A-Mode is immediate, B-Mode is any then modifier is AB`():
            List<Arguments> {
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

        private fun `generate modifier is always XXX`(): List<Arguments> {
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
    }
}
