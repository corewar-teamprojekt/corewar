package software.shonk.interpreter.instruction

import software.shonk.interpreter.AddressMode
import software.shonk.interpreter.Modifier
import software.shonk.interpreter.process.AbstractProcess

/** This instruction spawns a new process and adds it to the process queue of the program. */
class Split(
    aField: Int,
    bField: Int,
    addressModeA: AddressMode,
    addressModeB: AddressMode,
    modifier: Modifier,
) : AbstractInstruction(aField, bField, addressModeA, addressModeB, modifier) {
    override fun execute(process: AbstractProcess) {
        val startAddress = resolve(process, aField, addressModeA)
        process.program.addProcess(startAddress)
    }
}
