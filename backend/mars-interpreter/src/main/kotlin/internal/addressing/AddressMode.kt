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
    B_POST_INCREMENT,
}
