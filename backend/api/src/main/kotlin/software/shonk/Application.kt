package software.shonk

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import software.shonk.adapters.incoming.configureShorkInterpreterControllerV0
import software.shonk.adapters.incoming.configureShorkInterpreterControllerV1
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.service.ShorkService
import software.shonk.interpreter.IShork
import software.shonk.interpreter.Shork

fun main() {
    embeddedServer(Netty, port = 8080) {
            module()
            moduleApiV0()
            moduleApiV1()
            koinModule()
        }
        .start(wait = true)
}

fun Application.module() {
    // Install basic middleware like CORS and content negotiation here
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
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
    routing { route("/api/v0") { configureShorkInterpreterControllerV0() } }
}

fun Application.moduleApiV1() {
    routing { route("/api/v1") { configureShorkInterpreterControllerV1() } }
}

fun Application.koinModule() {
    koin {
        modules(
            module {
                single<IShork> { Shork() }
                single<ShorkUseCase> { ShorkService(get()) }
            }
        )
    }
}
