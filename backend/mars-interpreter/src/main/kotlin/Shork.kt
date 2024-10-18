package software.shonk.interpreter

internal class Shork : IShork {
    override fun run(settings: Settings, programs: Map<String, String>): String {
        return programs.keys.first()
    }
}
