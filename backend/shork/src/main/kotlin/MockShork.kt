package software.shonk.interpreter

/** This is just a fake interpreter, that lets the longer program win */
class MockShork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): String? {
        return programs
            .map { entry -> entry.key to entry.value.length }
            .maxByOrNull { it.second }
            ?.first
    }
}
