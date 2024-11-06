package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import software.shonk.application.port.incoming.ShorkUseCase

const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"
const val defaultLobby = 0L

fun Route.configureShorkInterpreterControllerV0() {
    val shorkUseCase by inject<ShorkUseCase>()

    get("/status") {
        val useCaseResponse = shorkUseCase.getLobbyStatus(defaultLobby)
        useCaseResponse.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        useCaseResponse.onSuccess { call.respond(it) }
    }

    post("/code/{player}") {
        val player = call.parameters["player"]
        val program = call.receive<String>()

        val result = shorkUseCase.addProgramToLobby(defaultLobby, player, program)
        result.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.OK) }
        return@post
    }
}

fun Route.configureShorkInterpreterControllerV1() {
    val shorkUseCase by inject<ShorkUseCase>()

    get("/lobby/{lobbyId}/code/{player}") {
        val player = call.parameters["player"]
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

        val program = shorkUseCase.getProgramFromLobby(lobbyId, player)

        program.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }

        program.onSuccess { call.respond(Program(it)) }
        return@get
    }

    post("/lobby") {
        val lobbyId = shorkUseCase.createLobby()
        val result = shorkUseCase.getLobbyStatus(lobbyId)

        result.onFailure {
            call.respond(HttpStatusCode.InternalServerError, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.Created, lobbyId.toString()) }
        return@post
    }

    get("lobby/status/{lobbyId}") {
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
        val lobbyStatus = shorkUseCase.getLobbyStatus(lobbyId)
        lobbyStatus.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        lobbyStatus.onSuccess { call.respond(HttpStatusCode.OK, it) }
    }
}

@Serializable data class Program(val code: String)
