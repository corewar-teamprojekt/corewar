package software.shonk.adapters.incoming

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.netty.handler.codec.http2.HttpConversionUtil.parseStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import software.shonk.module
import software.shonk.moduleApiV0
import software.shonk.moduleApiV1

class ShorkInterpreterControllerV1IT() : AbstractControllerTest() {

    override fun applyTestEngineApplication() {
        testEngine.application.apply {
            module()
            moduleApiV0() // @TODO: delete once v0 functionality is implemented in v1
            moduleApiV1()
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
    fun testGetPlayerCode() = runTest {
        // @TODO: "replace with v1 endpoint once it's implemented"
        client.post("/api/v0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        client.post("/api/v0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("someOtherString")
        }

        val result = client.get("/api/v1/lobby/0/code/playerA")
        assertEquals(
            "someString",
            Json.parseToJsonElement(result.bodyAsText()).jsonObject["code"]?.jsonPrimitive?.content,
        )

        val resultB = client.get("/api/v1/lobby/0/code/playerB")
        assertEquals(
            "someOtherString",
            Json.parseToJsonElement(resultB.bodyAsText()).jsonObject["code"]?.jsonPrimitive?.content,
        )
    }

    @Test
    fun testGetPlayerCodeNotSubmitted() = runTest {
        val result = client.get("/api/v1/lobby/0/code/playerA")
        assertEquals(HttpStatusCode.BadRequest, result.status)
        assert(result.bodyAsText().contains("No player with that name in the lobby"))
    }

    @Test
    fun `test get lobby status with invalid ID`() = runTest {
        val result = client.get("/api/v1/lobby/status/1")
        assertEquals(HttpStatusCode.BadRequest, result.status)
        assert(result.bodyAsText().contains("No lobby with that id"))
    }

    @Test
    fun `test get lobby status with valid (default) ID`() = runTest {
        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("false", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }
}
