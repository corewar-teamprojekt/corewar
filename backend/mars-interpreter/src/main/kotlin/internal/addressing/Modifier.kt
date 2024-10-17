package software.shonk.interpreter.internal.addressing

internal enum class Modifier {
    A,
    B,
    AB,
    BA,
    F,
    X,
    I;

    override fun toString(): String {
        return when (this) {
            A -> "A"
            B -> "B"
            AB -> "AB"
            BA -> "BA"
            F -> "F"
            X -> "X"
            I -> "I"
        }
    }
}
