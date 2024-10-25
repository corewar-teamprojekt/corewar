package software.shonk

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import software.shonk.adapters.incoming.configureShorkInterpreterController
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.service.ShorkService
import software.shonk.interpreter.IShork
import software.shonk.interpreter.MockShork

fun main() {
    embeddedServer(Netty, port = 8080) {
            module()
            moduleApiV0()
            koinModule()
        }
        .start(wait = true)
}

fun Application.module() {
    // Install basic middleware like CORS and content negotiation here
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                useArrayPolymorphism = false
            }
        )
    }
}

fun Application.moduleApiV0() {
    routing { route("/api/v0") { configureShorkInterpreterController() } }
}

fun Application.koinModule() {
    koin {
        modules(
            module {
                single<IShork> { MockShork() }
                single<ShorkUseCase> { ShorkService(get()) }
            }
        )
    }
}
