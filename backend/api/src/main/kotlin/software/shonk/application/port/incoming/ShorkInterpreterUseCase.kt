package software.shonk.application.port.incoming

import software.shonk.domain.Status
import software.shonk.interpreter.settings.AbstractSettings

interface ShorkInterpreterUseCase {
    fun addProgram(name: String, program: String)

    fun setSettings(settings: AbstractSettings)

    fun run()

    fun getStatus(): Status
}
