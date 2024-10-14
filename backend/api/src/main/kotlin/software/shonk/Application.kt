package software.shonk

import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Install basic middleware like CORS and content negotiation here
    configureApi()
}
