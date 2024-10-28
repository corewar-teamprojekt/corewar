package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import software.shonk.interpreter.internal.compiler.Scanner
import software.shonk.interpreter.internal.compiler.Token
import software.shonk.interpreter.internal.compiler.TokenType
import software.shonk.interpreter.internal.util.CircularQueue

internal class TestScanner {
    @ParameterizedTest
    @MethodSource("provideSingleLineValidPrograms")
    fun testScanSingleLinePrograms(program: String, expected: List<Token>) {
        val scanner = Scanner(program)
        val tokens = scanner.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
    }

    @ParameterizedTest
    @MethodSource("provideMultiLineProgram")
    fun testScanMultiLineProgram(program: String, expected: List<Token>) {
        val scanner = Scanner(program)
        val tokens = scanner.scanTokens()

        println("Program:\n$program")
        assertEquals(expected, tokens)
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

                            programs.add(Pair(program, expectedArgument))
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
            val theTokens = mutableListOf<Token>()

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
                theTokens.add(Token(instruction.second, instruction.first, "", line))

                // Modifier "DAT.I"
                if (modifier.second != null) {
                    allInstructionsAtLeastOnce.append(".${modifier.first} ")
                    theTokens.add(Token(TokenType.DOT, ".", "", line))
                    theTokens.add(Token(modifier.second!!, modifier.first, "", line))
                }

                // Space "DAT.I "
                allInstructionsAtLeastOnce.append(" ")

                // Address mode for first address "DAT.I $"
                if (addressMode.second != null) {
                    allInstructionsAtLeastOnce.append(addressMode.first)
                    theTokens.add(Token(addressMode.second!!, addressMode.first, "", line))
                }

                // First address "DAT.I $10"
                allInstructionsAtLeastOnce.append(addressA)
                theTokens.add(Token(TokenType.NUMBER, addressA.toString(), addressA, line))

                // Comma and space "DAT.I $10, "
                allInstructionsAtLeastOnce.append(", ")
                theTokens.add(Token(TokenType.COMMA, ",", "", line))

                // Address mode for second address "DAT.I $10 <"
                addressMode = addressModes.get()
                if (addressMode.second != null) {
                    allInstructionsAtLeastOnce.append(addressMode.first)
                    theTokens.add(Token(addressMode.second!!, addressMode.first, "", line))
                }

                // Second address "DAT.I $10 <20"
                allInstructionsAtLeastOnce.append(addressB)
                theTokens.add(Token(TokenType.NUMBER, addressB.toString(), addressB, line))

                // End of the line
                allInstructionsAtLeastOnce.append("\n")
                line++
            }

            // EOF token
            theTokens.add(Token(TokenType.EOF, "", "", line))

            return listOf(Arguments.of(allInstructionsAtLeastOnce.toString(), theTokens))
        }
    }
}
