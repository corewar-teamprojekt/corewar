package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import software.shonk.application.port.incoming.V0ShorkUseCase

fun Route.configureShorkInterpreterControllerV0() {
    val shorkUseCase by inject<V0ShorkUseCase>()

    /**
     * Returns either the status of the current game, or the status / result of the last game if
     * there is no currently active game.
     *
     * playerXSubmitted will only be true if code for player X has been submitted
     *
     * result will only be valid when gameState is FINISHED
     *
     * playerXSubmitted will switch back to false once the gameState switches to FINISHED
     */
    get("/status") { call.respond(shorkUseCase.getStatus()) }

    /**
     * Stores the program of a given player in current v0Lobby, creates that lobby if there is no
     * current v0Lobby. <br> Path Parameter - {player}: The name of the player whose code is being
     * submitted. For this endpoint ONLY 'playerA' and 'playerB' are valid! Body: The program of the
     * player, in plain text <br> Response 200: The game will automatically start once the second
     * player has submitted. Response 400: Game is in `RUNNING` state or a player still has to
     * submit code. The message field contains an explanation of what went wrong.
     *
     * { "message": String, }
     */
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
