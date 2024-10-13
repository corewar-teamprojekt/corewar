package software.shonk

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import software.shonk.adapters.incoming.configureHelloWorldController
import software.shonk.application.service.HelloWorldService

fun Application.configureApi() {
    val helloWorldUseCase = HelloWorldService()

    routing {
        route("/api") {
            configureHelloWorldController(helloWorldUseCase)
        }
    }
}