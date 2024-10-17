package software.shonk.interpreter.internal.addressing

internal enum class AddressMode {
    // #
    IMMEDIATE,
    // $
    DIRECT,
    // *
    A_INDIRECT,
    // @
    B_INDIRECT,
    // {
    A_PRE_DECREMENT,
    // }
    A_POST_INCREMENT,
    // <
    B_PRE_DECREMENT,
    // >
    B_POST_INCREMENT;

    override fun toString(): String {
        return when (this) {
            IMMEDIATE -> "# (Immediate)"
            DIRECT -> "$ (Direct)"
            A_INDIRECT -> "* (A Indirect)"
            B_INDIRECT -> "@ (B Indirect)"
            A_PRE_DECREMENT -> "{ (A Pre Decrement)"
            A_POST_INCREMENT -> "} (A Post Increment)"
            B_PRE_DECREMENT -> "< (B Pre Decrement)"
            B_POST_INCREMENT -> "> (B Post Increment)"
        }
    }
}
