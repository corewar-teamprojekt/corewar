package software.shonk

interface InterestingInterface {
    fun doSomething()

    fun doSomeMore(): String {
        return "Default impl :3"
    }

    fun doSomethingWithInput(input: String): String
}

class InterestingClass : InterestingInterface {
    override fun doSomething() {
        println("Doing something")
    }

    override fun doSomethingWithInput(input: String): String {
        return "I got: $input"
    }
}

fun main() {
    println("Hello World!")
}
