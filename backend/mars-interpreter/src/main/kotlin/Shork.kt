package software.shonk.interpreter

import software.shonk.interpreter.memory.ICore
import software.shonk.interpreter.program.AbstractProgram
import software.shonk.interpreter.settings.ISettings

class Shork : IShork {
    private val settings: ISettings
    private val programs: List<AbstractProgram>
    private val core: ICore

    constructor(
        settings: ISettings,
        programSources: Map<String, String>, // Mapping of User(ID) : Program (Source Code)
    ) {
        this.settings = settings

        // TODO: Construct AbstractPrograms
        // TODO: Construct Core according to provided settings
    }

    override fun run() {
        TODO("Not yet implemented")
    }
}
