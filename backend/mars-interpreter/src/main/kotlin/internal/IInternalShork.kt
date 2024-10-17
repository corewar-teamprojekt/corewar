package software.shonk.interpreter.internal

import software.shonk.interpreter.IShork
import software.shonk.interpreter.internal.memory.ICore

/**
 * Interface containing internal methods for the S.H.O.R.K interpreter. These functions are not
 * intended to be used by the downstream consumer of the library / interpreter.
 */
internal interface IInternalShork : IShork {
    fun getCore(): ICore
}
