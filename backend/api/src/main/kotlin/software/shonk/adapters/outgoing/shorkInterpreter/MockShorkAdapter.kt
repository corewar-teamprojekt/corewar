package software.shonk.adapters.outgoing.shorkInterpreter

import software.shonk.application.port.outgoing.ShorkPort
import software.shonk.interpreter.MockShork
import software.shonk.interpreter.Settings

class MockShorkAdapter(val id: Int) : ShorkPort {

    val shork = MockShork()
    var currentSettings: Settings = Settings(42, 100, "DAT", 100)

    override fun run(programs: Map<String, String>): String {
        return shork.run(currentSettings, programs)
    }

    override fun setSettings(settings: Settings) {
        this.currentSettings = settings
    }
}
