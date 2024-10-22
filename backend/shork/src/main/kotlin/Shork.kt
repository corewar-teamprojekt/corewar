package software.shonk.interpreter

class Shork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): String {
        return programs.keys.random()
    }
}
