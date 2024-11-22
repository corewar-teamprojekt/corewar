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
import org.slf4j.LoggerFactory
import software.shonk.application.port.incoming.ShorkUseCase

const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"

fun Route.configureShorkInterpreterControllerV1() {
    val logger = LoggerFactory.getLogger("ShorkInterpreterControllerV1")
    val shorkUseCase by inject<ShorkUseCase>()

    get("/lobby/{lobbyId}/code/{player}") {
        val player = call.parameters["player"]
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

        val program = shorkUseCase.getProgramFromLobby(lobbyId, player)

        program.onFailure {
            logger.error("Failed to get program from lobby", it)
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }

        program.onSuccess { call.respond(Program(it)) }
        return@get
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
        return@get
    }

    post("/lobby") {
        @Serializable data class CreateLobbyBody(val playerName: String)

        val createLobbyBody = call.receive<CreateLobbyBody>()

        val result = shorkUseCase.createLobby(createLobbyBody.playerName)
        result.onFailure {
            logger.error("Failed to create lobby, player name is invalid", it)
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.Created, it) }
    }

    post("/lobby/{lobbyId}/join") {
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

        val checkLobbyExists = shorkUseCase.getLobbyStatus(lobbyId)

        checkLobbyExists.onFailure {
            logger.error("The lobby you are trying to join doesn't exist", it)
            return@post call.respond(HttpStatusCode.NotFound)
        }

        @Serializable data class JoinLobbyBody(val playerName: String)

        val joinLobbyBody = call.receive<JoinLobbyBody>()

        val result = shorkUseCase.joinLobby(lobbyId, joinLobbyBody.playerName)
        result.onFailure {
            logger.error(
                "Someone already joined as that player. The slot is locked and the join operation is aborted",
                it,
            )
            return@post call.respond(HttpStatusCode.Conflict, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.OK, it) }
    }

    post("/lobby/{lobbyId}/code/{player}") {
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

        val player = call.parameters["player"]
        val program = call.receive<String>()
        val result = shorkUseCase.addProgramToLobby(lobbyId, player, program)

        result.onFailure {
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.OK) }
        return@post
    }

    get("/lobby") {
        val lobbiesStatus = shorkUseCase.getAllLobbies()
        call.respond(HttpStatusCode.OK, mapOf("lobbies" to lobbiesStatus))
        return@get
    }
}

@Serializable data class Program(val code: String)
