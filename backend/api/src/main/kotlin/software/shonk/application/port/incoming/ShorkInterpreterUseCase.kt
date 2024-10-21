package software.shonk.application.port.incoming

import software.shonk.domain.Status
import software.shonk.interpreter.Settings

interface ShorkInterpreterUseCase {
    fun addProgram(name: String, program: String)

    fun setSettings(settings: Settings)

    fun run()

    fun getStatus(): Status
}
