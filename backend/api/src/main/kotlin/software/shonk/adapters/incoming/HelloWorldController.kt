package software.shonk.adapters.incoming

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import software.shonk.application.port.HelloWorldUseCase

// Configure all endpoints regarding Hello World in here (example)
fun Route.configureHelloWorldController(helloWorldUseCase: HelloWorldUseCase) {
    get("/hello") { call.respondText(helloWorldUseCase.sayHello()) }
}

// Localized DTOs down here
// @Serializable, data class HelloWorldDTO...
