package software.shonk

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Install basic middleware like CORS and content negotiation here
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                classDiscriminator = "jsonType"
                prettyPrint = true
                useArrayPolymorphism = false
            }
        )
    }

    configureApi()
}
