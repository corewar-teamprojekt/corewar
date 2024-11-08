package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import software.shonk.interpreter.internal.compiler.Token
import software.shonk.interpreter.internal.compiler.TokenType
import software.shonk.interpreter.internal.compiler.Tokenizer
import software.shonk.interpreter.internal.util.CircularQueue

internal class TestTokenizer {
    @ParameterizedTest
    @MethodSource("provideSingleLineValidPrograms")
    fun testScanSingleLinePrograms(program: String, expected: List<Token>) {
        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        // if the columns match gets tested by a different test
        assertTokensEqualIgnoreColumns(expected, tokens)
    }

    @ParameterizedTest
    @MethodSource("provideMultiLineProgram")
    fun testScanMultiLineProgram(program: String, expected: List<Token>) {
        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        // if the columns match gets tested by a different test
        assertTokensEqualIgnoreColumns(expected, tokens)
    }

    @Test
    fun `test if tokenizer can handle single line only comment comments`() {
        val program = "; This is a comment"
        val expected =
            listOf(
                Token(TokenType.SEMICOLON, ";", "", 1, 1, 1),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test if tokenizer can handle instruction plus comment`() {
        val program = "DAT.X 42, #1337; This is a comment"
        val expected =
            listOf(
                Token(TokenType.DAT, "DAT", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.X, "X", "", 1, 5, 5),
                Token(TokenType.NUMBER, "42", 42L, 1, 7, 8),
                Token(TokenType.COMMA, ",", "", 1, 9, 9),
                Token(TokenType.HASHTAG, "#", "", 1, 11, 11),
                Token(TokenType.NUMBER, "1337", 1337L, 1, 12, 15),
                Token(TokenType.SEMICOLON, ";", "", 1, 16, 16),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test if tokenizer can handle empty program`() {
        val program = ""
        val expected = listOf(Token(TokenType.EOF, "", "", 1, 0, 0))

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct no modifiers, no address modes`() {
        val program = "DAT 10, 20"
        val expected =
            listOf(
                Token(TokenType.DAT, "DAT", "", 1, 1, 3),
                Token(TokenType.NUMBER, "10", 10L, 1, 5, 6),
                Token(TokenType.COMMA, ",", "", 1, 7, 7),
                Token(TokenType.NUMBER, "20", 20L, 1, 9, 10),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct no modifier, first address mode set`() {
        val program = "NOP $10, 20"
        val expected =
            listOf(
                Token(TokenType.NOP, "NOP", "", 1, 1, 3),
                Token(TokenType.DOLLAR, "$", "", 1, 5, 5),
                Token(TokenType.NUMBER, "10", 10L, 1, 6, 7),
                Token(TokenType.COMMA, ",", "", 1, 8, 8),
                Token(TokenType.NUMBER, "20", 20L, 1, 10, 11),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct no modifier, second address mode set`() {
        val program = "MOV 10 <20"
        val expected =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 1, 3),
                Token(TokenType.NUMBER, "10", 10L, 1, 5, 6),
                Token(TokenType.LOWER_THAN, "<", "", 1, 8, 8),
                Token(TokenType.NUMBER, "20", 20L, 1, 9, 10),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct no modifier, first and second address mode set`() {
        val program = "MOV $10 <20"
        val expected =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 1, 3),
                Token(TokenType.DOLLAR, "$", "", 1, 5, 5),
                Token(TokenType.NUMBER, "10", 10L, 1, 6, 7),
                Token(TokenType.LOWER_THAN, "<", "", 1, 9, 9),
                Token(TokenType.NUMBER, "20", 20L, 1, 10, 11),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with short modifier, no address modes`() {
        val program = "DAT.A 10, 20"
        val expected =
            listOf(
                Token(TokenType.DAT, "DAT", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.A, "A", "", 1, 5, 5),
                Token(TokenType.NUMBER, "10", 10L, 1, 7, 8),
                Token(TokenType.COMMA, ",", "", 1, 9, 9),
                Token(TokenType.NUMBER, "20", 20L, 1, 11, 12),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with short modifier, first address mode set`() {
        val program = "DAT.A $10, 20"
        val expected =
            listOf(
                Token(TokenType.DAT, "DAT", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.A, "A", "", 1, 5, 5),
                Token(TokenType.DOLLAR, "$", "", 1, 7, 7),
                Token(TokenType.NUMBER, "10", 10L, 1, 8, 9),
                Token(TokenType.COMMA, ",", "", 1, 10, 10),
                Token(TokenType.NUMBER, "20", 20L, 1, 12, 13),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with short modifier, second address mode set`() {
        val program = "JMP.I 10 <20"
        val expected =
            listOf(
                Token(TokenType.JMP, "JMP", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.I, "I", "", 1, 5, 5),
                Token(TokenType.NUMBER, "10", 10L, 1, 7, 8),
                Token(TokenType.LOWER_THAN, "<", "", 1, 10, 10),
                Token(TokenType.NUMBER, "20", 20L, 1, 11, 12),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with short modifier, both address modes set`() {
        val program = "JMZ.X }42, #14"
        val expected =
            listOf(
                Token(TokenType.JMZ, "JMZ", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.X, "X", "", 1, 5, 5),
                Token(TokenType.RIGHT_BRACE, "}", "", 1, 7, 7),
                Token(TokenType.NUMBER, "42", 42L, 1, 8, 9),
                Token(TokenType.COMMA, ",", "", 1, 10, 10),
                Token(TokenType.HASHTAG, "#", "", 1, 12, 12),
                Token(TokenType.NUMBER, "14", 14L, 1, 13, 14),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with long modifier, no address modes`() {
        val program = "SPL.AB 42, 20"
        val expected =
            listOf(
                Token(TokenType.SPL, "SPL", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.AB, "AB", "", 1, 5, 6),
                Token(TokenType.NUMBER, "42", 42L, 1, 8, 9),
                Token(TokenType.COMMA, ",", "", 1, 10, 10),
                Token(TokenType.NUMBER, "20", 20L, 1, 12, 13),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with long modifier, first address mode set`() {
        val program = "SPL.AB $10, 20"
        val expected =
            listOf(
                Token(TokenType.SPL, "SPL", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.AB, "AB", "", 1, 5, 6),
                Token(TokenType.DOLLAR, "$", "", 1, 8, 8),
                Token(TokenType.NUMBER, "10", 10L, 1, 9, 10),
                Token(TokenType.COMMA, ",", "", 1, 11, 11),
                Token(TokenType.NUMBER, "20", 20L, 1, 13, 14),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with long modifier, second address mode set`() {
        val program = "MOV.BA 10 <20"
        val expected =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.BA, "BA", "", 1, 5, 6),
                Token(TokenType.NUMBER, "10", 10L, 1, 8, 9),
                Token(TokenType.LOWER_THAN, "<", "", 1, 11, 11),
                Token(TokenType.NUMBER, "20", 20L, 1, 12, 13),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @Test
    fun `test columns correct with long modifier, both address modes set`() {
        val program = "MOV.BA $10 >20"
        val expected =
            listOf(
                Token(TokenType.MOV, "MOV", "", 1, 1, 3),
                Token(TokenType.DOT, ".", "", 1, 4, 4),
                Token(TokenType.BA, "BA", "", 1, 5, 6),
                Token(TokenType.DOLLAR, "$", "", 1, 8, 8),
                Token(TokenType.NUMBER, "10", 10L, 1, 9, 10),
                Token(TokenType.GREATER_THAN, ">", "", 1, 12, 12),
                Token(TokenType.NUMBER, "20", 20L, 1, 13, 14),
                Token(TokenType.EOF, "", "", 1, 0, 0),
            )

        val tokenizer = Tokenizer(program)
        val tokens = tokenizer.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    private fun assertTokensEqualIgnoreColumns(expected: List<Token>, actual: List<Token>) {
        val expected = expected.map { Token(it.type, it.lexeme, it.literal, it.line, 0, 0) }
        val actual = actual.map { Token(it.type, it.lexeme, it.literal, it.line, 0, 0) }

        assertEquals(expected, actual)
    }

    companion object {
        private val instructions =
            listOf(
                Pair("DAT", TokenType.DAT),
                Pair("NOP", TokenType.NOP),
                Pair("MOV", TokenType.MOV),
                Pair("ADD", TokenType.ADD),
                Pair("SUB", TokenType.SUB),
                Pair("MUL", TokenType.MUL),
                Pair("DIV", TokenType.DIV),
                Pair("MOD", TokenType.MOD),
                Pair("JMP", TokenType.JMP),
                Pair("JMZ", TokenType.JMZ),
                Pair("JMN", TokenType.JMN),
                Pair("DJN", TokenType.DJN),
                Pair("CMP", TokenType.CMP),
                Pair("SLT", TokenType.SLT),
                Pair("SPL", TokenType.SPL),
                Pair("ORG", TokenType.ORG),
                Pair("EQU", TokenType.EQU),
                Pair("END", TokenType.END),
                Pair("SEQ", TokenType.SEQ),
                Pair("SNE", TokenType.SNE),
                Pair("LDP", TokenType.LDP),
                Pair("STP", TokenType.STP),
            )

        private val modifiers =
            listOf(
                Pair("", null),
                Pair("A", TokenType.A),
                Pair("B", TokenType.B),
                Pair("AB", TokenType.AB),
                Pair("BA", TokenType.BA),
                Pair("F", TokenType.F),
                Pair("X", TokenType.X),
                Pair("I", TokenType.I),
            )

        private val addressModes =
            listOf(
                Pair("", null),
                Pair("$", TokenType.DOLLAR),
                Pair("#", TokenType.HASHTAG),
                Pair("*", TokenType.STAR),
                Pair("@", TokenType.AT),
                Pair("{", TokenType.LEFT_BRACE),
                Pair("<", TokenType.LOWER_THAN),
                Pair("}", TokenType.RIGHT_BRACE),
                Pair(">", TokenType.GREATER_THAN),
            )

        private fun generateAllPossibleInstructionPermutationsWithTokens():
            List<Pair<String, List<Token>>> {
            val instructions = instructions
            val modifiers = modifiers
            val addressModes = addressModes

            // Random address values, they only have to be consistent within the same test case
            val addressA = 10L
            val addressB = 20L

            val programs = mutableListOf<Pair<String, List<Token>>>()
            for (instruction in instructions) {
                for (modifier in modifiers) {
                    for (firstAddressMode in addressModes) {
                        for (secondAddressMode in addressModes) {
                            var program = instruction.first
                            val expectedArguments =
                                mutableListOf(
                                    Token(instruction.second, instruction.first, "", 1, 0, 0)
                                )

                            if (modifier.second != null) {
                                program += ".${modifier.first} "

                                expectedArguments += Token(TokenType.DOT, ".", "", 1, 0, 0)

                                expectedArguments +=
                                    Token(modifier.second!!, modifier.first, "", 1, 0, 0)
                            }

                            program += " "

                            if (firstAddressMode.second != null) {
                                program += firstAddressMode.first
                                expectedArguments +=
                                    Token(
                                        firstAddressMode.second!!,
                                        firstAddressMode.first,
                                        "",
                                        1,
                                        0,
                                        0,
                                    )
                            }

                            program += "$addressA"
                            expectedArguments +=
                                Token(TokenType.NUMBER, addressA.toString(), addressA, 1, 0, 0)

                            program += ","
                            expectedArguments += Token(TokenType.COMMA, ",", "", 1, 0, 0)

                            program += " "

                            if (secondAddressMode.second != null) {
                                program += secondAddressMode.first
                                expectedArguments +=
                                    Token(
                                        secondAddressMode.second!!,
                                        secondAddressMode.first,
                                        "",
                                        1,
                                        0,
                                        0,
                                    )
                            }

                            program += "$addressB"
                            expectedArguments +=
                                Token(TokenType.NUMBER, addressB.toString(), addressB, 1, 0, 0)

                            expectedArguments += Token(TokenType.EOF, "", "", 1, 0, 0)

                            programs.add(Pair(program, expectedArguments))
                        }
                    }
                }
            }

            return programs
        }

        @JvmStatic
        private fun provideSingleLineValidPrograms(): List<Arguments> {
            val instructionPermutations = generateAllPossibleInstructionPermutationsWithTokens()
            return instructionPermutations.map { Arguments.of(it.first, it.second) }
        }

        @JvmStatic
        private fun provideMultiLineProgram(): List<Arguments> {
            val allInstructionsAtLeastOnce = StringBuilder()
            val tokens = mutableListOf<Token>()

            val modifiers = CircularQueue(modifiers)
            val addressModes = CircularQueue(addressModes)
            var line = 1

            for (instruction in instructions) {
                val modifier = modifiers.get()
                var addressMode = addressModes.get()
                val addressA = 10L
                val addressB = 20L

                // Instruction "DAT"
                allInstructionsAtLeastOnce.append(instruction.first)
                tokens.add(Token(instruction.second, instruction.first, "", line, 0, 0))

                // Modifier "DAT.I"
                if (modifier.second != null) {
                    allInstructionsAtLeastOnce.append(".${modifier.first} ")
                    tokens.add(Token(TokenType.DOT, ".", "", line, 0, 0))
                    tokens.add(Token(modifier.second!!, modifier.first, "", line, 0, 0))
                }

                // Space "DAT.I "
                allInstructionsAtLeastOnce.append(" ")

                // Address mode for first address "DAT.I $"
                if (addressMode.second != null) {
                    allInstructionsAtLeastOnce.append(addressMode.first)
                    tokens.add(Token(addressMode.second!!, addressMode.first, "", line, 0, 0))
                }

                // First address "DAT.I $10"
                allInstructionsAtLeastOnce.append(addressA)
                tokens.add(Token(TokenType.NUMBER, addressA.toString(), addressA, line, 0, 0))

                // Comma and space "DAT.I $10, "
                allInstructionsAtLeastOnce.append(", ")
                tokens.add(Token(TokenType.COMMA, ",", "", line, 0, 0))

                // Address mode for second address "DAT.I $10 <"
                addressMode = addressModes.get()
                if (addressMode.second != null) {
                    allInstructionsAtLeastOnce.append(addressMode.first)
                    tokens.add(Token(addressMode.second!!, addressMode.first, "", line, 0, 0))
                }

                // Second address "DAT.I $10 <20"
                allInstructionsAtLeastOnce.append(addressB)
                tokens.add(Token(TokenType.NUMBER, addressB.toString(), addressB, line, 0, 0))

                // End of the line
                allInstructionsAtLeastOnce.append("\n")
                line++
            }

            // EOF token
            tokens.add(Token(TokenType.EOF, "", "", line, 0, 0))

            return listOf(Arguments.of(allInstructionsAtLeastOnce.toString(), tokens))
        }
    }
}
