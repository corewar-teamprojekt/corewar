package software.shonk.interpreter.internal.statistics

data class RoundInformation(
    val playerId: String,
    val programCounterBefore: Int,
    val programCounterAfter: Int,
    val programCountersOfOtherProcesses: List<Int>,
    val memoryReads: List<Int>,
    val memoryWrites: List<Int>,
    val processDied: Boolean,
)
