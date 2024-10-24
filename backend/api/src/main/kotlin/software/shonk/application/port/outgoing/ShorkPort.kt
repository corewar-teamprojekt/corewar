package software.shonk.application.port.outgoing

import software.shonk.interpreter.Settings

interface ShorkPort {
    fun run(programs: Map<String, String>): String

    fun setSettings(settings: Settings)
}
