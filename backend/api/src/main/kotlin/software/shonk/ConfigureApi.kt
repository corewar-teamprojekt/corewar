package software.shonk

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import software.shonk.adapters.incoming.configureHelloWorldController
import software.shonk.adapters.incoming.configureShorkInterpreterController
import software.shonk.adapters.outgoing.shorkInterpreter.MockShorkAdapter
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.port.outgoing.ShorkPort
import software.shonk.application.service.HelloWorldService
import software.shonk.application.service.ShorkService

fun Application.configureApi() {
    val shorkPort: ShorkPort = MockShorkAdapter()

    val helloWorldUseCase = HelloWorldService()
    val shorkUseCase: ShorkUseCase = ShorkService(shorkPort)

    routing { route("/api") { configureHelloWorldController(helloWorldUseCase) } }
    routing { route("/api/v0") { configureShorkInterpreterController(shorkUseCase) } }
}
