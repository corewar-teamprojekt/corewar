package software.shonk.domain

import kotlinx.serialization.Serializable

@Serializable
enum class Winner {
    A,
    B,
    UNDECIDED,
}
