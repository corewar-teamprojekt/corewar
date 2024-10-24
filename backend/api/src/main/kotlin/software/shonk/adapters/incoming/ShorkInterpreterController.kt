package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import software.shonk.application.port.incoming.ShorkUseCase

fun Route.configureShorkInterpreterController(shorkUseCase: ShorkUseCase) {
    get("/status") { call.respond(shorkUseCase.getStatus()) }

    post("/code/{player}") {
        val player = call.parameters["player"]

        if (player == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (player != "playerA" && player != "playerB") {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val program = call.receive<String>()
        shorkUseCase.addProgram(player, program)
        call.respond(HttpStatusCode.OK)
        return@post
    }
}
