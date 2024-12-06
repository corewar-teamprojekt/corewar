package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
data class InterpreterSettings(
    val coreSize: Int,
    val instructionLimit: Int,
    val initialInstruction: String,
    val maximumTicks: Int,
    val maximumProcessesPerPlayer: Int,
    val readDistance: Int,
    val writeDistance: Int,
    val minimumSeparation: Int,
    val separation: Int,
    val randomSeparation: Boolean,
)
