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

const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"

fun Route.configureShorkInterpreterControllerV0() {
    val shorkUseCase by inject<ShorkUseCase>()

    get("/status") {
        val useCaseResponse = shorkUseCase.getLobbyStatus(0L)
        useCaseResponse.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        useCaseResponse.onSuccess { call.respond(it) }
    }

    post("/code/{player}") {
        val player = call.parameters["player"]
        val program = call.receive<String>()

        val result = shorkUseCase.addProgramToLobby(0L, player, program)
        result.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.OK) }
        return@post
    }
}
