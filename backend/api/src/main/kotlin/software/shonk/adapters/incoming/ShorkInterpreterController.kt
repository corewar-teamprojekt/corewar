package software.shonk.adapters.incoming

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import software.shonk.application.port.incoming.GetLobbyStatusQuery
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.domain.Player

const val UNKNOWN_ERROR_MESSAGE = "Unknown Error"

fun Route.configureShorkInterpreterControllerV1() {
    val logger = LoggerFactory.getLogger("ShorkInterpreterControllerV1")
    val shorkUseCase by inject<ShorkUseCase>()
    val getLobbyStatusQuery by inject<GetLobbyStatusQuery>()

    /**
     * Path params:
     * - lobbyId: The id of the lobby, whose status you want to get.
     * - Type: number
     *
     * <br>
     *
     * Query params:
     * - showVisualizationData: Whether to include visualization data in the response.
     *     - Default: true
     *     - Type: boolean
     *
     * Returns either the status of the current game in a lobby or the status of the last finished
     * game in a lobby. Path parameter - {lobbyId}: The id of the lobby, which status you want to
     * get. Keep in mind, that in this api version there is no default lobby!
     *
     * playerXSubmitted will only be true if code for player X has been submitted.
     *
     * Result will only be valid when gameState is FINISHED, beforehand it is undefined.
     *
     * playerXSubmitted will switch back to false once the gameState switches to FINISHED and a
     * player in the lobby submits new code to the lobby.
     *
     * Response 400: The lobby id is invalid, so the id wasn't given as the path parameter OR the
     * lobby you are trying to get the status from, doesn't exist.
     *
     * Response 200: The GET request is valid and the lobby status including the visualization data
     * is returned.
     *
     * <br>
     *
     * response:
     *
     * <br>
     *
     * { "playerASubmitted": boolean, "playerBSubmitted": boolean, "gameState": One of
     * [NOT_STARTED, RUNNING, FINISHED], "result": { "winner": One of [A, B, DRAW], },
     * "visualizationData": [ { "playerId": One of [A, B], "programCounterBefore": number,
     * "programCounterAfter": number, "programCountersOfOtherProcesses": number, "memoryReads":
     * number, "memoryWrites": number, "processDied": boolean, } ] }
     */
    get("lobby/{lobbyId}/status") {
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

        val showVisualizationData =
            call.request.queryParameters["showVisualizationData"]?.toBoolean() ?: true

        val lobbyStatus = getLobbyStatusQuery.getLobbyStatus(lobbyId, showVisualizationData)
        lobbyStatus.onFailure {
            logger.error("No lobby with that id exists.")
            return@get call.respond(HttpStatusCode.NotFound, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }
        lobbyStatus.onSuccess { call.respond(HttpStatusCode.OK, it) }
        return@get
    }

    /**
     * Posts the player code to a specific lobby, which is specified in the path parameters. If the
     * gamestate of the game in the specified lobby is FINISHED, then the lobby gets reset, so a new
     * game can be played and the new code gets submitted correctly. Path parameter - {lobbyId}: The
     * lobby id which the player code is to be submitted. Path parameter - {player}: The player path
     * variable has to be one of [A,B].
     *
     * The body must contain the code that is to be submitted. body: { "code": String, }
     *
     * Response 201: The post operation was successful.
     *
     * Response 400: An incorrect player has been specified in the path/request. body: { "message":
     * String, }
     *
     * Response 404: The lobby doesn't exist.
     */
    post("/lobby/{lobbyId}/code/{player}") {
        val lobbyId =
            call.parameters["lobbyId"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

        val playerName = call.parameters["player"]
        val submitCodeRequest = call.receive<SubmitCodeRequest>()

        if (getLobbyStatusQuery.getLobbyStatus(lobbyId).isFailure || playerName == null) {
            return@post call.respond(HttpStatusCode.NotFound)
        }

        val result =
            kotlin
                .runCatching { Player(playerName) }
                .mapCatching { shorkUseCase.addProgramToLobby(lobbyId, it, submitCodeRequest.code) }

        result.onFailure {
            logger.error(
                "Failed to add program to lobby, error on service layer after passing command!",
                it,
            )
            // todo change this to internal server error or at least re-evaluate
            call.respond(HttpStatusCode.BadRequest, it.message ?: UNKNOWN_ERROR_MESSAGE)
        }

        result.onSuccess { wrappedResult ->
            wrappedResult.onFailure {
                call.respond(HttpStatusCode.Forbidden, it.message ?: UNKNOWN_ERROR_MESSAGE)
            }
            wrappedResult.onSuccess { call.respond(HttpStatusCode.OK) }
        }
    }
}

@Serializable data class Program(val code: String)

@Serializable data class SubmitCodeRequest(val code: String)
