package compiler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.compiler.Compiler
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
}
