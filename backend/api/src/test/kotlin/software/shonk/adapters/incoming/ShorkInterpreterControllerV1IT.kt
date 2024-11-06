package software.shonk.adapters.incoming

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
        val expectedStatus =
            """
        {
            "playerASubmitted": false,
            "playerBSubmitted": false,
            "gameState": "NOT_STARTED",
            "result": {
                "winner": "UNDECIDED"
            }
        }"""
                .trimIndent()
        assertEquals(expectedStatus, result.bodyAsText())
    }
}
