package software.shonk.interpreter

import software.shonk.interpreter.settings.AbstractSettings

class MockShork : IShork {
    var longest = ""

    override fun run(programs: Map<String, String>, settings: AbstractSettings) {
        var length = 0
        for ((name, program) in programs) {
            if (program.length > length) {
                longest = name
                length = program.length
            }
        }
    }

    fun getResult(): String {
        return longest
    }
}
