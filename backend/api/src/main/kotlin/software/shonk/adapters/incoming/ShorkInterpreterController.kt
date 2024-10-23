package software.shonk.adapters.incoming

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import software.shonk.application.port.incoming.ShorkInterpreterUseCase

// Function to configure routes for the ShorkInterpreterController
fun Route.configureShorkInterpreterController(shorkInterpreterUseCase: ShorkInterpreterUseCase) {
    // GET endpoint to retrieve the status of the ShorkInterpreter
    get("/status") {
        // Respond with the current status converted to a string
        call.respond(shorkInterpreterUseCase.getStatus())
    }

    // POST endpoint to submit code for a specific player
    post("/code/{player}") {
        // Extract the player identifier from the URL parameters
        val player = call.parameters["player"]

        // Respond with a Bad Request status if the player identifier is invalid
        if (player == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (player != "playerA" && player != "playerB") {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        // Add the received program to the Shork Interpreter for the specified player
        val program = call.receive<String>()
        shorkInterpreterUseCase.addProgram(player, program)
        // Respond with an OK status indicating successful processing
        call.respond(HttpStatusCode.OK)
        return@post
    }
}
