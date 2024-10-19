package software.shonk.interpreter.internal

/** Interface for cloneable objects. */
internal interface ICloneable<T> {
    /** Clones the object, creating a deep copy. */
    fun deepCopy(): T
}
