package compiler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import software.shonk.interpreter.internal.compiler.Compiler

internal class TestCompiler {
    @Test
    fun `test empty program, no errors, no instructions generated`() {
        val sourceCode = ""
        val compiler = Compiler(sourceCode)

        assertFalse(compiler.errorsOccured)
        assertTrue(compiler.instructions.isEmpty())
    }

    @Test
    fun `test program with only comments, no errors, no instructions generated`() {
        val sourceCode =
            """
            ; woop woop
            ; this is a comment
            ; Shonks are cute
            ; Get a blahaj
            ; This is an order.
        """
        val compiler = Compiler(sourceCode)

        assertFalse(compiler.errorsOccured)
        assertTrue(compiler.instructions.isEmpty())
    }

    @Test
    fun `test simple program, no errors generated`() {
        val sourceCode =
            """
            MOV 0, 1
        """
        val compiler = Compiler(sourceCode)

        assertFalse(compiler.errorsOccured)
        assertTrue(compiler.instructions.isNotEmpty())
    }

    @Test
    fun `test complex program, no errors generated`() {
        val sourceCode =
            """
            MOV.AB $0, }1
            SPL.X <0, #42
            DAT.I 0, 1 ; This is very important to kill our program
        """
        val compiler = Compiler(sourceCode)

        assertFalse(compiler.errorsOccured)
        assertTrue(compiler.instructions.isNotEmpty())
    }

    @Test
    fun `test simple program with errors`() {
        val sourceCode = "Mov 0 1"
        val compiler = Compiler(sourceCode)

        assertTrue(compiler.errorsOccured)
        assertTrue(compiler.instructions.isEmpty())
    }

    @Test
    fun `test complex program with errors`() {
        val sourceCode =
            """
            MOV.AB $0, }1
            SPL.X <0, #42
            DAT.I 0, 1 
            MOV 0 1,, ; Invalid instruction / line
            JMP #23, &43 ; Invalid instruction / line (address modifier & doesn't exist)
            Mov.F #42, $45
        """
        val compiler = Compiler(sourceCode)

        assertTrue(compiler.errorsOccured)
        assertTrue(compiler.instructions.isNotEmpty())
        assertEquals(5, compiler.instructions.count())
    }
}
