package software.shonk.interpreter

class MockShork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): String {
        var longest = ""
        var length = 0
        for ((name, program) in programs) {
            if (program.length > length) {
                longest = name
                length = program.length
            }
        }

        return longest
    }
}
