package software.shonk.interpreter.internal.statistics

import software.shonk.interpreter.internal.instruction.AbstractInstruction
import software.shonk.interpreter.internal.process.AbstractProcess
import software.shonk.interpreter.internal.program.AbstractProgram

internal class GameDataCollector : IGameDataCollector {
    private var currentProgram: AbstractProgram? = null
    private var roundInformation = mutableListOf<RoundInformation>()

    private var memoryReads = mutableListOf<Int>()
    private var memoryWrites = mutableListOf<Int>()
    private var programCounterBeforeTick = -1
    private var programCounterAfterTick = -1
    private var otherProcesses = listOf<AbstractProcess>()
    private var processDied = false

    override fun startRoundForProgram(program: AbstractProgram) {
        currentProgram = program
    }

    override fun endRoundForProgram(program: AbstractProgram) {
        val roundInfo =
            RoundInformation(
                program.playerId,
                programCounterBeforeTick,
                programCounterAfterTick,
                otherProcesses.map { it.programCounter },
                memoryReads,
                memoryWrites,
                processDied,
            )

        this.roundInformation.add(roundInfo)

        resetValues()
    }

    override fun collectProcessDataBeforeTick(process: AbstractProcess) {
        this.programCounterBeforeTick = process.programCounter
        otherProcesses = currentProgram!!.processes.all().filter { it !== process }
    }

    override fun collectProcessDataAfterTick(process: AbstractProcess) {
        this.programCounterAfterTick = process.programCounter
        this.processDied = process.program.processes.all().contains(process).not()
    }

    override fun collectMemoryRead(absoluteAddress: Int) {
        this.memoryReads.add(absoluteAddress)
    }

    override fun collectMemoryWrite(absoluteAddress: Int, instruction: AbstractInstruction) {
        this.memoryWrites.add(absoluteAddress)
    }

    override fun getGameStatistics(): List<RoundInformation> {
        return this.roundInformation
    }

    private fun resetValues() {
        memoryReads = mutableListOf()
        memoryWrites = mutableListOf()
        programCounterBeforeTick = -1
        programCounterAfterTick = -1
        otherProcesses = mutableListOf()
        processDied = false
    }

    override fun toString(): String {
        return "GameDataCollector(currentProgram=$currentProgram, roundInformation=$roundInformation, memoryReads=$memoryReads, memoryWrites=$memoryWrites, programCounterBeforeTick=$programCounterBeforeTick, programCounterAfterTick=$programCounterAfterTick, otherProcesses=$otherProcesses)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as GameDataCollector

        if (currentProgram != other.currentProgram) return false
        if (roundInformation != other.roundInformation) return false
        if (memoryReads != other.memoryReads) return false
        if (memoryWrites != other.memoryWrites) return false
        if (programCounterBeforeTick != other.programCounterBeforeTick) return false
        if (programCounterAfterTick != other.programCounterAfterTick) return false
        if (otherProcesses != other.otherProcesses) return false
        if (processDied != other.processDied) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentProgram?.hashCode() ?: 0
        result = 31 * result + roundInformation.hashCode()
        result = 31 * result + memoryReads.hashCode()
        result = 31 * result + memoryWrites.hashCode()
        result = 31 * result + programCounterBeforeTick
        result = 31 * result + programCounterAfterTick
        result = 31 * result + otherProcesses.hashCode()
        result = 31 * result + processDied.hashCode()
        return result
    }
}
