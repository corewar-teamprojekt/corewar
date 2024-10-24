package software.shonk.interpreter.internal.parser

internal fun main() {
    // val input = "MOV.AB #10, $20"
    // val input = "MOV.AB #10, $20\nMOV.I 42 <69"
    val input = "MOV.AB #10, $20\nDAT.I 42 <69\nCMP.F }42, <420"
    val scanner = Scanner(input)

    val start = System.currentTimeMillis()
    val tokens = scanner.scanTokens()
    val end = System.currentTimeMillis()
    println("Took ${end - start} ms")

    println("Errors while scanning:")
    scanner.errors.forEach { println(it) }

    val parser = Parser(tokens)
    val instructions = parser.parse()
    println("Instructions: ")
    instructions.forEach {
        println(
            "$it, modifier: ${it.modifier}, afield: ${it.aField} mode A: ${it.addressModeA}, bfield: ${it.bField} mode B: ${it.addressModeB}"
        )
    }

    println("Errors while parsing:")
    parser.errors.forEach { println(it) }
}
