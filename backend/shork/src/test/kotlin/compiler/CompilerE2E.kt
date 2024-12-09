package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.compiler.Compiler
import software.shonk.interpreter.internal.instruction.Add
import software.shonk.interpreter.internal.instruction.Dat
import software.shonk.interpreter.internal.instruction.Jmp
import software.shonk.interpreter.internal.instruction.Mov

internal class CompilerE2E {
    @Test
    fun `test imp`() {
        val code =
            """
            ;name: imp
            MOV 0, 1
        """
                .trimIndent()
        val compiler = Compiler(code)

        assertTrue(compiler.instructions.isNotEmpty())
        assertTrue(compiler.tokenizerErrors.isEmpty())
        assertTrue(compiler.parserErrors.isEmpty())

        val expected = listOf(Mov(0, 1, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.I))
        assertEquals(expected, compiler.instructions)
    }

    @Test
    fun `test dwarf`() {
        val code =
            """
            ;name: Dwarf
            ADD #4, 3
            MOV 2, @2
            JMP -2
            DAT #0, #0
        """
                .trimIndent()
        val compiler = Compiler(code)

        assertTrue(compiler.instructions.isNotEmpty())
        assertTrue(compiler.tokenizerErrors.isEmpty())
        assertTrue(compiler.parserErrors.isEmpty())

        val expected =
            listOf(
                Add(4, 3, AddressMode.IMMEDIATE, AddressMode.DIRECT, Modifier.AB),
                Mov(2, 2, AddressMode.DIRECT, AddressMode.B_INDIRECT, Modifier.I),
                Jmp(-2, 0, AddressMode.DIRECT, AddressMode.DIRECT, Modifier.B),
                Dat(0, 0, AddressMode.IMMEDIATE, AddressMode.IMMEDIATE, Modifier.F),
            )
        assertEquals(expected, compiler.instructions)
    }
}
