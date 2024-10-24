package software.shonk.interpreter.internal.parser

internal fun main() {
    // val input = "MOV.AB #10, $20"
    // val input = "MOV.AB #10, $20\nMOV.I 42 <69"
    val input =
        "MOV.AB #10, $20\n" + // Line 1
            "DAT.I 42 <69\n" + // Line 2
            "CMP.F }42, <420\n" + // Line 3
            "DJN.A 43, 92\n" + // Line 4
            "JMP #42, {1337\n" + // Line 5
            "CMP $35, *69\n" + // No Modifier // Line 6
            ""
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
