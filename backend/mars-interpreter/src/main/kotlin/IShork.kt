package software.shonk.interpreter

import software.shonk.interpreter.memory.ICore

interface IShork {
    fun run()

    fun getCore(): ICore
}
