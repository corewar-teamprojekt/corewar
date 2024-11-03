package software.shonk.adapters.incoming

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import software.shonk.module
import software.shonk.moduleApiV0

class ShorkInterpreterControllerV0IT() : AbstractControllerTest() {

    override fun applyTestEngineApplication() {
        testEngine.application.apply {
            module()
            moduleApiV0()
        }
    }

    private suspend fun parseStatus(response: HttpResponse): Map<String, String> {
        val responseJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val result = responseJson["result"]?.jsonObject
        val resultWinner = result?.get("winner")?.jsonPrimitive?.content ?: "UNDECIDED"
        return mapOf(
            "playerASubmitted" to responseJson["playerASubmitted"]!!.jsonPrimitive.content,
            "playerBSubmitted" to responseJson["playerBSubmitted"]!!.jsonPrimitive.content,
            "gameState" to responseJson["gameState"]!!.jsonPrimitive.content,
            "result.winner" to resultWinner,
        )
    }

    @Test
    fun testGetStatus() = runTest {
        val response = client.get("/api/v0/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetStatusDefault() = runTest {
        val response = client.get("/api/v0/status")
        val responseData = parseStatus(response)
        assertEquals("false", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun testPostPlayerCodeValidUsername() = runTest {
        val player = "playerA"
        val response = client.post("/api/v0/code/$player")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostPlayerCodeInvalidUsername() = runTest {
        val player = "playerC"
        val response = client.post("/api/v0/code/$player")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testPostPlayerCodeValid() = runTest {
        val player = "playerA"
        val response =
            client.post("/api/v0/code/$player") {
                contentType(ContentType.Application.Json)
                setBody("somestring")
            }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPlayerSubmitted() = runTest {
        val player = "playerA"
        client.post("/api/v0/code/$player") {
            contentType(ContentType.Application.Json)
            setBody("somestring")
        }

        val response = client.get("/api/v0/status")
        val responseData = parseStatus(response)
        assertEquals("true", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun testInvalidPlayerSubmitted() = runTest {
        val player = "playerC"
        client.post("/api/v0/code/$player") {
            contentType(ContentType.Application.Json)
            setBody("somestring")
        }

        val response = client.get("/api/v0/status")
        val responseData = parseStatus(response)
        assertEquals("false", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun testBothPlayersSubmittedAndGameStartsAndLongerProgramWins() = runTest {
        client.post("/api/v0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("looooooooong")
        }

        client.post("/api/v0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("short")
        }

        val response = client.get("/api/v0/status")
        val responseData = parseStatus(response)

        assertNotEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("A", responseData["result.winner"])
    }

    @Test
    fun testBothPlayersSubmittedGameGetsSimulatedPlayerCodeAndFlagsResetAfterNewPlayerCodeGetsSubmitted() =
        runTest {
            client.post("/api/v0/code/playerA") {
                contentType(ContentType.Application.Json)
                setBody("someString")
            }

            client.post("/api/v0/code/playerB") {
                contentType(ContentType.Application.Json)
                setBody("someOtherString")
            }
            client.post("/api/v0/code/playerA") {
                contentType(ContentType.Application.Json)
                setBody("someNewString")
            }

            // check if code/flags were reset after running and game hasn't started
            val response = client.get("/api/v0/status")
            val resopnseData = parseStatus(response)
            assertEquals("true", resopnseData["playerASubmitted"])
            assertEquals("false", resopnseData["playerBSubmitted"])
            assertEquals("NOT_STARTED", resopnseData["gameState"])
            assertEquals("UNDECIDED", resopnseData["result.winner"])
        }
}
