package software.shonk.interpreter.internal.util

/** A queue that cycles through its elements. */
internal class CircularQueue<T> {
    private val queue = ArrayDeque<T>()

    constructor()

    constructor(items: List<T>) {
        queue.addAll(items)
    }

    constructor(vararg items: T) {
        queue.addAll(items)
    }

    /** Add an item to the queue. */
    fun add(item: T) {
        queue.add(item)
    }

    /** Get the next item in the queue. */
    fun get(): T {
        val item = queue.removeFirst()
        queue.add(item)
        return item
    }

    /** Remove an item from the queue by reference. Removing an item leaves no hole. */
    fun removeByReference(item: T) {
        val idx = indexOfByReference(item)
        if (idx != -1) {
            queue.removeAt(idx)
        }
    }

    /** Get the index of an item in the queue by reference. */
    fun indexOfByReference(item: T): Int {
        queue.forEachIndexed { index, element ->
            if (element === item) {
                return index
            }
        }

        return -1
    }

    /** Returns the next item without moving it to the back of the queue */
    fun first(): T {
        return queue.first()
    }

    /** Returns the last item without moving anything around */
    fun last(): T {
        return queue.last()
    }

    /** Returns all items in the queue as an immutable list */
    fun all(): List<T> {
        return queue
    }

    /** Kinda self-explanatory, isn't it? */
    fun size() = queue.size

    /** Returns if the queue is empty. */
    fun isEmpty() = queue.isEmpty()
}
