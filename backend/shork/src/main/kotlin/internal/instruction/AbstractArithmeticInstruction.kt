package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.memory.ResolvedAddresses
import software.shonk.interpreter.internal.process.AbstractProcess

/** Abstracts away common logic between all arithmetic instructions (ADD, SUB, MUL, DIV, MOD) */
internal abstract class AbstractArithmeticInstruction(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    // Used to track if an error (such as divide by zero) has occured
    // so that it may be handled later
    var errorOccured = false

    // Wrapper function that executes the operation, but only
    // sets the errorOccured flag when an exception was thrown.
    // With this, even if an exception is thrown during an instruction that does two operations,
    // the other operation still gets executed.
    fun executeWithHandling(f: () -> Int): Int? {
        try {
            return f()
        } catch (e: ArithmeticException) {
            errorOccured = true
            return null
        }
    }

    override fun execute(process: AbstractProcess, resolvedAddresses: ResolvedAddresses) {
        val core = process.program.shork.memoryCore
        val sourceAddress = resolvedAddresses.aFieldRead
        val destinationAddress = resolvedAddresses.bFieldRead
        val destinationWriteAddress = resolvedAddresses.bFieldWrite

        val sourceInstruction = core.loadAbsolute(sourceAddress)
        val destinationInstruction = core.loadAbsolute(destinationAddress)
        var destinationWriteInstruction = core.loadAbsolute(destinationWriteAddress)

        errorOccured = false

        when (modifier) {
            Modifier.A -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.aField,
                        )
                    })

                result?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.aField = result
                    }
                }
            }
            Modifier.B -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.bField,
                        )
                    })

                result?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.bField = result
                    }
                }
            }
            Modifier.AB -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.bField,
                        )
                    })

                result?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.bField = result
                    }
                }
            }
            Modifier.BA -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.aField,
                        )
                    })

                result?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.aField = result
                    }
                }
            }
            Modifier.F,
            Modifier.I -> {
                val resultA =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.aField,
                        )
                    })

                resultA?.let {
                    destinationWriteInstruction =
                        destinationWriteInstruction.write { it.aField = resultA }.first
                }

                val resultB =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.bField,
                        )
                    })

                resultB?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.bField = resultB
                    }
                }
            }
            Modifier.X -> {
                val result1 =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.bField,
                        )
                    })

                result1?.let {
                    destinationWriteInstruction =
                        destinationWriteInstruction.write { it.bField = result1 }.first
                }

                val result2 =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.aField,
                        )
                    })

                result2?.let {
                    destinationWriteInstruction.writeToMemory(core, destinationAddress) {
                        it.aField = result2
                    }
                }
            }
        }

        if (errorOccured) {
            process.program.removeProcess(process)
        }
    }

    /**
     * Executes an arithmetic operation given two operands.
     *
     * @param sourceInstructionOperand The first operand
     * @param destinationInstructionOperand The second operand
     * @return The result of the operation
     * @throws ArithmeticException When an illegal operation (e.g. divide by zero) is performed
     */
    abstract fun runArithmeticOperation(
        sourceInstructionOperand: Int,
        destinationInstructionOperand: Int,
    ): Int
}
