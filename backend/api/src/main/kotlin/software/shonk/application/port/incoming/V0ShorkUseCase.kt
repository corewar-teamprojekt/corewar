package software.shonk.application.port.incoming

import software.shonk.domain.V0Status
import software.shonk.interpreter.Settings

interface V0ShorkUseCase {
    fun addProgram(name: String, program: String)

    fun setSettings(settings: Settings)

    fun run()

    fun getStatus(): V0Status
}
