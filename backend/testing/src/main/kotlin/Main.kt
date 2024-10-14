package software.shonk.testing

import software.shonk.interpreter.InterestingClass
import software.shonk.interpreter.InterestingInterface

fun main() {
    val interestingInterface: InterestingInterface = InterestingClass()
    val out = interestingInterface.doSomethingWithInput("testing :3")

    println(out)
}