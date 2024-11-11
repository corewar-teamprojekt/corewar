package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import software.shonk.application.port.incoming.ShorkUseCase

const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"
const val defaultLobby = 0L

fun Route.configureShorkInterpreterControllerV0() {
    val logger = LoggerFactory.getLogger("ShorkInterpreterControllerV0")

    val shorkUseCase by inject<ShorkUseCase>()

    get("/status") {
        val tempUseCaseResponse = shorkUseCase.getLobbyStatus(defaultLobby)
        tempUseCaseResponse.onFailure { shorkUseCase.createLobby("playerA") }
        val useCaseResponse = shorkUseCase.getLobbyStatus(defaultLobby)
        useCaseResponse.onFailure {
            logger.error("Failed to get lobby status", it)
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        useCaseResponse.onSuccess { call.respond(it) }
    }

    post("/code/{player}") {
        val player = call.parameters["player"]
        val program = call.receive<String>()

        val tempUseCaseResponse = shorkUseCase.getLobbyStatus(defaultLobby)
        tempUseCaseResponse.onFailure {
            if (player != null) {
                shorkUseCase.createLobby(player)
            }
        }

        val result = shorkUseCase.addProgramToLobby(defaultLobby, player, program)
        result.onFailure {
            logger.error("Failed to add program to lobby", it)
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.OK) }
        return@post
    }
}

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
        val creatingPlayerName = call.receive<String>()
        val json = Json { ignoreUnknownKeys = true }
        val jsonObj = json.parseToJsonElement(creatingPlayerName).jsonObject
        val name = jsonObj["playerName"]?.jsonPrimitive?.content.toString()
        val result = shorkUseCase.createLobby(name)
        result.onFailure {
            logger.error("Failed to create lobby, player name is invalid", it)
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        result.onSuccess { call.respond(HttpStatusCode.Created, it) }
        return@post
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
}

@Serializable data class Program(val code: String)
