package software.shonk.interpreter.internal.instruction

import software.shonk.interpreter.internal.addressing.AddressMode
import software.shonk.interpreter.internal.addressing.Modifier
import software.shonk.interpreter.internal.process.AbstractProcess

/** Abstracts away common logic between all arithmetic instructions (ADD, SUB, MUL, DIV, MOD) */
internal abstract class AbstractArithmeticInstruction(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {

    override fun execute(process: AbstractProcess) {
        val core = process.program.shork.memoryCore
        val sourceAddress = resolve(process, aField, addressModeA)
        val destinationAddress = resolve(process, bField, addressModeB)
        val sourceInstruction = core.loadAbsolute(sourceAddress)
        val destinationInstruction = core.loadAbsolute(destinationAddress)

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

        when (modifier) {
            Modifier.A -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.aField,
                        )
                    })

                result?.run({ destinationInstruction.aField = result })
            }
            Modifier.B -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.bField,
                        )
                    })

                result?.run({ destinationInstruction.bField = result })
            }
            Modifier.AB -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.bField,
                        )
                    })

                result?.run({ destinationInstruction.bField = result })
            }
            Modifier.BA -> {
                val result =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.aField,
                        )
                    })

                result?.run({ destinationInstruction.aField = result })
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

                resultA?.run({ destinationInstruction.aField = resultA })

                val resultB =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.bField,
                        )
                    })

                resultB?.run({ destinationInstruction.bField = resultB })
            }
            Modifier.X -> {
                val result1 =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.aField,
                            destinationInstruction.bField,
                        )
                    })

                result1?.run({ destinationInstruction.bField = result1 })

                val result2 =
                    executeWithHandling({
                        runArithmeticOperation(
                            sourceInstruction.bField,
                            destinationInstruction.aField,
                        )
                    })

                result2?.run({ destinationInstruction.aField = result2 })
            }
        }

        // If any exception was thrown, remove the process.
        if (errorOccured) {
            process.program.removeProcess(process)
        }
    }

    /**
     * Executes an arithmetic operation given two operands.
     *
     * @param operand1 The first operand
     * @param operand2 The second operand
     * @return The result of the operation
     * @throws ArithmeticException When an illegal operation (e.g. divide by zero) is performed
     */
    abstract fun runArithmeticOperation(operand1: Int, operand2: Int): Int
}
