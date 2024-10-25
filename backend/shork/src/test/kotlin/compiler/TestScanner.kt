package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import software.shonk.interpreter.internal.parser.Scanner
import software.shonk.interpreter.internal.parser.Token
import software.shonk.interpreter.internal.parser.TokenType

internal class TestScanner {
    @ParameterizedTest
    @MethodSource("provideSingleLineValidPrograms")
    fun testScan(program: String, expected: List<Token>) {
        val scanner = Scanner(program)
        val tokens = scanner.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    companion object {
        private val instructions =
            listOf(
                Pair("DAT", TokenType.DAT),
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

        @JvmStatic
        private fun provideSingleLineValidPrograms(): List<Arguments> {
            val instructions = instructions
            val modifiers = modifiers
            val addressModes = addressModes

            // Random address values, they only have to be consistent within the same test case
            val addressA = 10L
            val addressB = 20L

            val programs = mutableListOf<Arguments>()
            for (instruction in instructions) {
                for (modifier in modifiers) {
                    for (firstAddressMode in addressModes) {
                        for (secondAddressMode in addressModes) {
                            var program = instruction.first
                            val expectedArgument =
                                mutableListOf(Token(instruction.second, instruction.first, "", 1))

                            if (modifier.second != null) {
                                program += ".${modifier.first} "
                                expectedArgument += Token(TokenType.DOT, ".", "", 1)
                                expectedArgument += Token(modifier.second!!, modifier.first, "", 1)
                            }

                            program += " "

                            if (firstAddressMode.second != null) {
                                program += firstAddressMode.first
                                expectedArgument +=
                                    Token(firstAddressMode.second!!, firstAddressMode.first, "", 1)
                            }
                            program += "$addressA, "
                            expectedArgument +=
                                Token(TokenType.NUMBER, addressA.toString(), addressA, 1)

                            expectedArgument += Token(TokenType.COMMA, ",", "", 1)
                            if (secondAddressMode.second != null) {
                                program += secondAddressMode.first
                                expectedArgument +=
                                    Token(
                                        secondAddressMode.second!!,
                                        secondAddressMode.first,
                                        "",
                                        1,
                                    )
                            }
                            program += "$addressB "
                            expectedArgument +=
                                Token(TokenType.NUMBER, addressB.toString(), addressB, 1)

                            expectedArgument += Token(TokenType.EOF, "", "", 1)

                            programs.add(Arguments.of(program, expectedArgument))
                        }
                    }
                }
            }

            return programs
        }
    }
}
