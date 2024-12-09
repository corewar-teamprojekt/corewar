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
    fun `parser can parse negative numbers as fields`() {
        val program =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 0, 0),
                Token(TokenType.DOT, ".", "", 1, 0, 0),
                Token(TokenType.F, "F", "", 1, 0, 0),
                Token(TokenType.MINUS, "-", "", 1, 0, 0),
                Token(TokenType.NUMBER, "42", 42, 1, 0, 0),
                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                Token(TokenType.MINUS, "-", "", 1, 0, 0),
                Token(TokenType.NUMBER, "1337", 1337, 1, 0, 0),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val expected = listOf(Mov(-42, -1337, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.F))

        val parser = Parser(program)
        val instructions = parser.parse()

        assertEquals(expected, instructions)
    }

    @Test
    fun `parser allows missing B-Field`() {
        val program =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 0, 0),
                Token(TokenType.DOT, ".", "", 1, 0, 0),
                Token(TokenType.B, "B", "", 1, 0, 0),
                Token(TokenType.NUMBER, "42", 42, 1, 0, 0),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val expected = listOf(Mov(42, 0, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B))

        val parser = Parser(program)
        val instructions = parser.parse()

        assertEquals(expected, instructions)
    }

    @Test
    fun `parser allows missing B-Field with an instruction afterwards`() {
        val program =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 0, 0),
                Token(TokenType.DOT, ".", "", 1, 0, 0),
                Token(TokenType.B, "B", "", 1, 0, 0),
                Token(TokenType.NUMBER, "42", 42, 1, 0, 0),
                Token(TokenType.DJN, "MOV", "", 1, 0, 0),
                Token(TokenType.DOT, ".", "", 1, 0, 0),
                Token(TokenType.X, "B", "", 1, 0, 0),
                Token(TokenType.NUMBER, "69", 42, 1, 0, 0),
                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                Token(TokenType.NUMBER, "1337", 0, 1, 0, 0),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val expected =
            listOf(
                Mov(42, 0, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B),
                Djn(69, 1337, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.X),
            )

        val parser = Parser(program)
        val instructions = parser.parse()

        assertEquals(expected, instructions)
    }

    @Test
    fun `CMP is alias for SEQ`() {
        val program =
            listOf(
                Token(TokenType.CMP, "CMP", "", 1, 0, 0),
                Token(TokenType.NUMBER, "42", 42, 1, 0, 0),
                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                Token(TokenType.NUMBER, "1337", 1337, 1, 0, 0),
                Token(TokenType.I, "I", "", 1, 0, 0),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val expected = listOf(Seq(42, 1337, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I))

        val parser = Parser(program)
        val instructions = parser.parse()

        assertEquals(expected, instructions)
    }

    @ParameterizedTest
    @MethodSource("provideValidAllInstructionsWithAllModifiersAndAllAddressModesOnce")
    fun `test if every instruction can be combined with every modifier`(
        program: List<Token>,
        expected: List<AbstractInstruction>,
    ) {
        val parser = Parser(program)
        val instructions = parser.parse()

        if (parser.parsingErrors.isNotEmpty()) {
            println("Errors while parsing:")
            parser.parsingErrors.forEach { error(it) }
        }

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

        if (parser.parsingErrors.isNotEmpty()) {
            println("Errors while parsing:")
            parser.parsingErrors.forEach { error(it) }
        }

        assertEquals(expected, instructions)
    }

    @ParameterizedTest
    @MethodSource("provideDefaultModifierTests")
    fun testDefaultModifierHandling(program: List<Token>, expected: AbstractInstruction) {
        val instruction = Parser(program).parse().first()

        assertEquals(expected, instruction)
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

        private val modifiers =
            listOf(
                Triple(Modifier.A, "A", TokenType.A),
                Triple(Modifier.B, "B", TokenType.B),
                Triple(Modifier.AB, "AB", TokenType.AB),
                Triple(Modifier.BA, "BA", TokenType.BA),
                Triple(Modifier.F, "F", TokenType.F),
                Triple(Modifier.X, "X", TokenType.X),
                Triple(Modifier.I, "I", TokenType.I),
            )

        // Order does not matter, but for readability purposes, should be the same as in
        // TokenType.instructions()
        private val instructions =
            listOf(
                Triple(Dat::class, "DAT", TokenType.DAT),
                Triple(Nop::class, "NOP", TokenType.NOP),
                Triple(Mov::class, "MOV", TokenType.MOV),
                Triple(Add::class, "ADD", TokenType.ADD),
                Triple(Sub::class, "SUB", TokenType.SUB),
                Triple(Mul::class, "MUL", TokenType.MUL),
                Triple(Div::class, "DIV", TokenType.DIV),
                Triple(Mod::class, "MOD", TokenType.MOD),
                Triple(Jmp::class, "JMP", TokenType.JMP),
                Triple(Jmz::class, "JMZ", TokenType.JMZ),
                Triple(Jmn::class, "JMN", TokenType.JMN),
                Triple(Djn::class, "DJN", TokenType.DJN),
                Triple(Seq::class, "CMP", TokenType.CMP),
                Triple(Sne::class, "SNE", TokenType.SNE),
                Triple(Slt::class, "SLT", TokenType.SLT),
                Triple(Spl::class, "SPL", TokenType.SPL),
                // No Org here
                // No EQU here
                // No END here
                // No LDP here (yet)
                // No STP here (yet)
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
                                    Token(
                                        instruction.first,
                                        instruction.first.toString(),
                                        "",
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(firstMode.third, firstMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        firstAddress.toString(),
                                        firstAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                    Token(secondMode.third, secondMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        secondAddress.toString(),
                                        secondAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.EOF, "", "", 1, 0, 0),
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
                                Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                                Token(firstAddressMode.third, firstAddressMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    firstAddress.toString(),
                                    firstAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                Token(
                                    secondAddressMode.third,
                                    secondAddressMode.second,
                                    "",
                                    1,
                                    0,
                                    0,
                                ),
                                Token(
                                    TokenType.NUMBER,
                                    secondAddress.toString(),
                                    secondAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.EOF, "", "", 1, 0, 0),
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
                                    Token(
                                        instruction.first,
                                        instruction.first.toString(),
                                        "",
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(firstMode.third, firstMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        firstAddress.toString(),
                                        firstAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                    Token(secondMode.third, secondMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        secondAddress.toString(),
                                        secondAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.EOF, "", "", 1, 0, 0),
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
                    .map { Triple(it.first, it.second, Modifier.AB) }
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
                                Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                                Token(firstMode.third, firstMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    firstAddress.toString(),
                                    firstAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                Token(secondMode.third, secondMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    secondAddress.toString(),
                                    secondAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.EOF, "", "", 1, 0, 0),
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
                    Triple(TokenType.JMP, Jmp::class, Modifier.B),
                    Triple(TokenType.JMZ, Jmz::class, Modifier.B),
                    Triple(TokenType.JMN, Jmn::class, Modifier.B),
                    Triple(TokenType.DJN, Djn::class, Modifier.B),
                    Triple(TokenType.SPL, Spl::class, Modifier.B),
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
                                    Token(
                                        instruction.first,
                                        instruction.first.toString(),
                                        "",
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(firstMode.third, firstMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        firstAddress.toString(),
                                        firstAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                    Token(secondMode.third, secondMode.second, "", 1, 0, 0),
                                    Token(
                                        TokenType.NUMBER,
                                        secondAddress.toString(),
                                        secondAddress,
                                        1,
                                        0,
                                        0,
                                    ),
                                    Token(TokenType.EOF, "", "", 1, 0, 0),
                                ),
                                listOf(instance),
                            )
                        )
                    }
                }
            }

            return arguments
        }

        @JvmStatic
        private fun provideValidAllInstructionsWithAllModifiersAndAllAddressModesOnce():
            List<Arguments> {
            val firstAddress = 42
            val secondAddress = 1337

            val arguments = mutableListOf<Arguments>()
            for (instruction in instructions) {
                for (modifier in modifiers) {
                    for (firstAddressMode in addressModes) {
                        for (secondAddressMode in addressModes) {
                            val instance =
                                instruction.first.constructors
                                    .first()
                                    .call(
                                        firstAddress,
                                        secondAddress,
                                        firstAddressMode.first,
                                        secondAddressMode.first,
                                        modifier.first,
                                    )

                            arguments.add(
                                Arguments.of(
                                    listOf(
                                        Token(instruction.third, instruction.second, "", 1, 0, 0),
                                        Token(TokenType.DOT, ".", "", 1, 0, 0),
                                        Token(modifier.third, modifier.second, "", 1, 0, 0),
                                        Token(
                                            firstAddressMode.third,
                                            firstAddressMode.second,
                                            "",
                                            1,
                                            0,
                                            0,
                                        ),
                                        Token(
                                            TokenType.NUMBER,
                                            firstAddress.toString(),
                                            firstAddress,
                                            1,
                                            0,
                                            0,
                                        ),
                                        Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                        Token(
                                            secondAddressMode.third,
                                            secondAddressMode.second,
                                            "",
                                            1,
                                            0,
                                            0,
                                        ),
                                        Token(
                                            TokenType.NUMBER,
                                            secondAddress.toString(),
                                            secondAddress,
                                            1,
                                            0,
                                            0,
                                        ),
                                        Token(TokenType.EOF, "", "", 1, 0, 0),
                                    ),
                                    listOf(instance),
                                )
                            )
                        }
                    }
                }
            }

            return arguments
        }

        @JvmStatic
        fun provideDefaultModifierTests(): List<Arguments> {
            return provideAllSimpleDefaultModifier() +
                `provide A Mode immediate, B whatever then Modifier is AB`() +
                `provide B-Mode immediate, A-Mode whatever then Modifier is B`() +
                `provide neither address mode is immediate`() +
                `provide A not immediate, B whatever then Modifier is B`()
        }

        // Some instructions always get the same default modifier, no matter the address modes
        private fun provideAllSimpleDefaultModifier(): List<Arguments> {
            val inputWithExpected = mutableListOf<Pair<List<Token>, AbstractInstruction>>()

            val firstAddress = 42
            val secondAddress = 1337

            val instructionWithModifier =
                listOf(
                    // DAT and NOP always get F, no matter the address modes
                    Triple(TokenType.DAT, Dat::class, Modifier.F),
                    Triple(TokenType.NOP, Nop::class, Modifier.F),
                    // JMP, JMZ, JMN, DJN, SPL always get B, no matter the address modes
                    Triple(TokenType.JMP, Jmp::class, Modifier.B),
                    Triple(TokenType.JMZ, Jmz::class, Modifier.B),
                    Triple(TokenType.JMN, Jmn::class, Modifier.B),
                    Triple(TokenType.DJN, Djn::class, Modifier.B),
                    Triple(TokenType.SPL, Spl::class, Modifier.B),
                )

            for (instruction in instructionWithModifier) {
                for (firstAddressMode in addressModes) {
                    for (secondAddressMode in addressModes) {
                        val tokens =
                            listOf(
                                Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                                Token(firstAddressMode.third, firstAddressMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    firstAddress.toString(),
                                    firstAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                Token(
                                    secondAddressMode.third,
                                    secondAddressMode.second,
                                    "",
                                    1,
                                    0,
                                    0,
                                ),
                                Token(
                                    TokenType.NUMBER,
                                    secondAddress.toString(),
                                    secondAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.EOF, "", "", 1, 0, 0),
                            )

                        val expectedInstance =
                            instruction.second.constructors
                                .first()
                                .call(
                                    firstAddress,
                                    secondAddress,
                                    firstAddressMode.first,
                                    secondAddressMode.first,
                                    instruction.third,
                                )

                        inputWithExpected.add(Pair(tokens, expectedInstance))
                    }
                }
            }

            return inputWithExpected.map { Arguments.of(it.first, it.second) }
        }

        // MOV, SEQ, SNE, CMP, ADD, SUB, MUL, DIV, MOD, SLT, LDP, STP
        private fun `provide A Mode immediate, B whatever then Modifier is AB`(): List<Arguments> {
            val inputWithExpected = mutableListOf<Pair<List<Token>, AbstractInstruction>>()

            val firstAddress = 42
            val secondAddress = 1337

            val instructionWithModifier =
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
                    Pair(TokenType.SLT, Slt::class),
                    Pair(TokenType.LDP, Ldp::class),
                    Pair(TokenType.STP, Stp::class),
                )

            for (instruction in instructionWithModifier) {
                for (addressMode in addressModes) {
                    val tokens =
                        listOf(
                            Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                            Token(TokenType.HASHTAG, "#", "", 1, 0, 0),
                            Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1, 0, 0),
                            Token(TokenType.COMMA, ",", "", 1, 0, 0),
                            Token(addressMode.third, addressMode.second, "", 1, 0, 0),
                            Token(
                                TokenType.NUMBER,
                                secondAddress.toString(),
                                secondAddress,
                                1,
                                0,
                                0,
                            ),
                            Token(TokenType.EOF, "", "", 1, 0, 0),
                        )

                    val expectedInstance =
                        instruction.second.constructors
                            .first()
                            .call(
                                firstAddress,
                                secondAddress,
                                AddressMode.IMMEDIATE,
                                addressMode.first,
                                Modifier.AB,
                            )

                    inputWithExpected.add(Pair(tokens, expectedInstance))
                }
            }

            return inputWithExpected.map { Arguments.of(it.first, it.second) }
        }

        // MOV, SEQ, SNE, CMP, ADD, SUB, MUL, DIV, MOD, SLT, LDP, STP
        private fun `provide B-Mode immediate, A-Mode whatever then Modifier is B`():
            List<Arguments> {
            val inputWithExpected = mutableListOf<Pair<List<Token>, AbstractInstruction>>()

            val firstAddress = 42
            val secondAddress = 1337

            val instructionWithModifier =
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

            for (instruction in instructionWithModifier) {
                for (addressMode in addressModes.filter { it.first != AddressMode.IMMEDIATE }) {
                    val tokens =
                        listOf(
                            Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                            Token(addressMode.third, addressMode.second, "", 1, 0, 0),
                            Token(TokenType.NUMBER, firstAddress.toString(), firstAddress, 1, 0, 0),
                            Token(TokenType.COMMA, ",", "", 1, 0, 0),
                            Token(TokenType.HASHTAG, "#", "", 1, 0, 0),
                            Token(
                                TokenType.NUMBER,
                                secondAddress.toString(),
                                secondAddress,
                                1,
                                0,
                                0,
                            ),
                            Token(TokenType.EOF, "", "", 1, 0, 0),
                        )

                    val expectedInstance =
                        instruction.second.constructors
                            .first()
                            .call(
                                firstAddress,
                                secondAddress,
                                addressMode.first,
                                AddressMode.IMMEDIATE,
                                Modifier.B,
                            )

                    inputWithExpected.add(Pair(tokens, expectedInstance))
                }
            }

            return inputWithExpected.map { Arguments.of(it.first, it.second) }
        }

        // MOV, SEQ, SNE, CMP, ADD, SUB, MUL, DIV, MOD
        private fun `provide neither address mode is immediate`(): List<Arguments> {
            val inputWithExpected = mutableListOf<Pair<List<Token>, AbstractInstruction>>()
            val firstAddress = 42
            val secondAddress = 1337

            val instructionWithModifier =
                listOf(
                    // MOV, SEQ, SNE, CMP are always I
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

            val addressModesNoImmediate = addressModes.filter { it.first != AddressMode.IMMEDIATE }

            for (instruction in instructionWithModifier) {
                for (firstAddressMode in addressModesNoImmediate) {
                    for (secondAddressMode in addressModesNoImmediate) {
                        val tokens =
                            listOf(
                                Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                                Token(firstAddressMode.third, firstAddressMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    firstAddress.toString(),
                                    firstAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                Token(
                                    secondAddressMode.third,
                                    secondAddressMode.second,
                                    "",
                                    1,
                                    0,
                                    0,
                                ),
                                Token(
                                    TokenType.NUMBER,
                                    secondAddress.toString(),
                                    secondAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.EOF, "", "", 1, 0, 0),
                            )

                        val expectedInstance =
                            instruction.second.constructors
                                .first()
                                .call(
                                    firstAddress,
                                    secondAddress,
                                    firstAddressMode.first,
                                    secondAddressMode.first,
                                    instruction.third,
                                )

                        inputWithExpected.add(Pair(tokens, expectedInstance))
                    }
                }
            }

            return inputWithExpected.map { Arguments.of(it.first, it.second) }
        }

        private fun `provide A not immediate, B whatever then Modifier is B`(): List<Arguments> {
            val inputWithExpected = mutableListOf<Pair<List<Token>, AbstractInstruction>>()
            val firstAddress = 42
            val secondAddress = 1337

            val instructionWithModifier =
                listOf(
                    // SLT, LDP, STP are always B
                    Triple(TokenType.SLT, Slt::class, Modifier.B),
                    Triple(TokenType.LDP, Ldp::class, Modifier.B),
                    Triple(TokenType.STP, Stp::class, Modifier.B),
                )

            val addressModesNoImmediate = addressModes.filter { it.first != AddressMode.IMMEDIATE }

            for (instruction in instructionWithModifier) {
                for (firstAddressMode in addressModesNoImmediate) {
                    for (secondAddressMode in addressModes) {
                        val tokens =
                            listOf(
                                Token(instruction.first, instruction.first.toString(), "", 1, 0, 0),
                                Token(firstAddressMode.third, firstAddressMode.second, "", 1, 0, 0),
                                Token(
                                    TokenType.NUMBER,
                                    firstAddress.toString(),
                                    firstAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.COMMA, ",", "", 1, 0, 0),
                                Token(
                                    secondAddressMode.third,
                                    secondAddressMode.second,
                                    "",
                                    1,
                                    0,
                                    0,
                                ),
                                Token(
                                    TokenType.NUMBER,
                                    secondAddress.toString(),
                                    secondAddress,
                                    1,
                                    0,
                                    0,
                                ),
                                Token(TokenType.EOF, "", "", 1, 0, 0),
                            )

                        val expectedInstance =
                            instruction.second.constructors
                                .first()
                                .call(
                                    firstAddress,
                                    secondAddress,
                                    firstAddressMode.first,
                                    secondAddressMode.first,
                                    instruction.third,
                                )

                        inputWithExpected.add(Pair(tokens, expectedInstance))
                    }
                }
            }

            return inputWithExpected.map { Arguments.of(it.first, it.second) }
        }
    }
}
