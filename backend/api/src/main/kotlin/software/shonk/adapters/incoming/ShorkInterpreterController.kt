package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import software.shonk.application.port.incoming.ShorkUseCase

fun Route.configureShorkInterpreterControllerV0() {
    val shorkUseCase by inject<ShorkUseCase>()

    get("/status") {
        val useCaseResponse = shorkUseCase.getLobbyStatus(0L)
        if (useCaseResponse == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            call.respond(useCaseResponse)
        }
    }

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
        shorkUseCase.addProgramToLobby(0L, player, program)
        call.respond(HttpStatusCode.OK)
        return@post
    }
}
