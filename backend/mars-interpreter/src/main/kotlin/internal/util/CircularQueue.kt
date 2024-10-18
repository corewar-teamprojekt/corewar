package software.shonk.interpreter.internal.util

internal class CircularQueue<T> {
    private val queue = ArrayDeque<T>()

    fun add(item: T) {
        queue.add(item)
    }

    fun get(): T {
        val item = queue.removeFirst()
        queue.add(item)
        return item
    }

    fun removeByReference(item: T) {
        val idx = indexOfByReference(item)
        if (idx != -1) {
            queue.removeAt(idx)
        }
    }

    fun indexOfByReference(item: T): Int {
        queue.forEachIndexed { index, element ->
            if (element === item) {
                return index
            }
        }

        return -1
    }

    fun size() = queue.size
}
