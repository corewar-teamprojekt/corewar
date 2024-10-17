package software.shonk.adapters.incoming

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import software.shonk.application.port.ShorkInterpreterUseCase

// Configure all endpoints regarding Hello World in here (example)
fun Route.configureShorkInterpreterController(shorkInterpreterUseCase: ShorkInterpreterUseCase) {
    get("/status") { call.respondText(shorkInterpreterUseCase.getStatus().toString()) }
}

// Localized DTOs down here
// @Serializable, data call lass HelloWorldDTO...
