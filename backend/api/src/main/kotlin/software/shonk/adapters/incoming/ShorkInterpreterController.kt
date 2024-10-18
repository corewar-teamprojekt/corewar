package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import software.shonk.application.port.ShorkInterpreterUseCase

// Configure all endpoints regarding Hello World in here (example)
fun Route.configureShorkInterpreterController(shorkInterpreterUseCase: ShorkInterpreterUseCase) {
    get("/status") { call.respondText(shorkInterpreterUseCase.getStatus().toString()) }
    post("/code/{player}") {
        val player = call.parameters["player"]
        if (player == null || player == "") {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val program = call.receive<String>()
            shorkInterpreterUseCase.addProgram(player, program)
            call.respond(HttpStatusCode.OK)
        }
    }
}

// Localized DTOs down here
// @Serializable, data call lass HelloWorldDTO...
