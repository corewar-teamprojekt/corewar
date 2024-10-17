package software.shonk

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import software.shonk.adapters.incoming.configureHelloWorldController
import software.shonk.adapters.incoming.configureShorkInterpreterController
import software.shonk.adapters.outgoing.configureShorkInterpreterControllerOutgoing
import software.shonk.application.service.HelloWorldService
import software.shonk.application.service.MockShorkInterpreterService

fun Application.configureApi() {
    val helloWorldUseCase = HelloWorldService()
    val shorkInterpreterUseCase = MockShorkInterpreterService()

    routing { route("/api") { configureHelloWorldController(helloWorldUseCase) } }
    routing {
        route("/api/v0") {
            configureShorkInterpreterController(shorkInterpreterUseCase)
            configureShorkInterpreterControllerOutgoing(shorkInterpreterUseCase)
        }
    }
}
