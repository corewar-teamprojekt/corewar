package software.shonk.adapters.incoming

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.assertNotNull
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
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
    fun `test post and get player code`() = runTest {
        client.post("/api/v1/lobby/0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        client.post("/api/v1/lobby/0/code/playerB") {
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
    fun `test get lobby status with valid default ID`() = runTest {
        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("false", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun `test get lobby status with valid custom ID`() = runTest {
        client.post("/api/v1/lobby/0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        client.post("/api/v1/lobby/0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("someOtherString")
        }

        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("true", responseData["playerASubmitted"])
        assertEquals("true", responseData["playerBSubmitted"])
        assertEquals("FINISHED", responseData["gameState"])
        assertEquals("B", responseData["result.winner"])
    }

    @Test
    fun `test player empty code submission`() = runTest {
        val player = "playerA"
        val result = client.post("/api/v1/lobby/0/code/$player")
        assertEquals(HttpStatusCode.OK, result.status)
    }

    @Test
    fun `test player code submission with invalid username does not change lobby status`() =
        runTest {
            val player = "playerC"
            client.post("/api/v1/lobby/0/code/$player") {
                contentType(ContentType.Application.Json)
                setBody("someString")
            }

            val result = client.get("/api/v1/lobby/0/code/playerC")
            assertEquals(HttpStatusCode.BadRequest, result.status)
            assert(result.bodyAsText().contains("No player with that name in the lobby"))

            val resultStatus = client.get("/api/v1/lobby/status/0")
            val responseData = parseStatus(resultStatus)
            assertEquals("false", responseData["playerASubmitted"])
            assertEquals("false", responseData["playerBSubmitted"])
            assertEquals("NOT_STARTED", responseData["gameState"])
            assertEquals("UNDECIDED", responseData["result.winner"])
        }

    @Test
    fun `test player code submission reflecting in lobby status`() = runTest {
        val player = "playerA"
        client.post("/api/v1/lobby/0/code/$player") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("true", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun `test game starts when both players submit`() = runTest {
        client.post("/api/v1/lobby/0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someVeryLongString")
        }

        client.post("/api/v1/lobby/0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("someShortString")
        }

        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("FINISHED", responseData["gameState"])
    }

    @Test
    fun `test lobby status resets after new code submission by player A`() = runTest {
        client.post("/api/v1/lobby/0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        client.post("/api/v1/lobby/0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("someOtherString")
        }
        client.post("/api/v1/lobby/0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someNewString")
        }

        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("true", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun `test create a new lobby`() = runTest {
        val result = client.post("/api/v1/lobby")
        assertEquals(HttpStatusCode.Created, result.status)
        assertEquals("1", result.bodyAsText())
    }

    @Test
    fun `test create a multiple new lobbies`() = runTest {
        val result = client.post("/api/v1/lobby")
        val result2 = client.post("/api/v1/lobby")
        val result3 = client.post("/api/v1/lobby")
        assertEquals(HttpStatusCode.Created, result.status)
        assertEquals("1", result.bodyAsText())
        assertEquals(HttpStatusCode.Created, result2.status)
        assertEquals("2", result2.bodyAsText())
        assertEquals(HttpStatusCode.Created, result3.status)
        assertEquals("3", result3.bodyAsText())
    }

    @kotlinx.serialization.Serializable
    data class LobbyInformation(
        val lobbyId: Long,
        val playersJoined: List<String>,
        val gameState: String,
    )

    private suspend fun parseAllLobbiesResponse(
        httpResponse: HttpResponse
    ): MutableList<LobbyInformation> {
        val body = httpResponse.bodyAsText()
        val jsonObject = Json.parseToJsonElement(body).jsonObject

        assert(jsonObject.containsKey("lobbies"))
        return Json.decodeFromJsonElement<MutableList<LobbyInformation>>(jsonObject["lobbies"]!!)
    }

    suspend fun createLobby(client: HttpClient): Long {
        val result = client.post("/api/v1/lobby")
        assertEquals(HttpStatusCode.Created, result.status)

        return result.bodyAsText().toLong()
    }

    @Test
    fun `get all lobbies returns only default lobby if none have been created`() = runTest {
        val result = client.get("/api/v1/lobby")

        assertEquals(HttpStatusCode.OK, result.status)

        val lobbies = parseAllLobbiesResponse(result)

        assertEquals(1, lobbies.size)
        assertEquals(LobbyInformation(0, emptyList(), "NOT_STARTED"), lobbies.first())
    }

    @Test
    fun `get all lobbies returns multiple lobbies`() = runTest {
        val lobbyIds = List(3) { createLobby(client) }

        val allLobbiesResponse = client.get("/api/v1/lobby")

        assertEquals(HttpStatusCode.OK, allLobbiesResponse.status)

        val lobbies = parseAllLobbiesResponse(allLobbiesResponse).toMutableList()

        assertEquals(4, lobbies.size) // 3 we created + 1 default
        assertEquals(LobbyInformation(0, emptyList(), "NOT_STARTED"), lobbies.first())
        assertNotEquals(lobbies[1].lobbyId, lobbies[2].lobbyId)
        assertNotEquals(lobbies[2].lobbyId, lobbies[3].lobbyId)

        for (lobbyId in lobbyIds) {
            val lobby = lobbies.find { it.lobbyId == lobbyId }
            assertNotNull(lobby)

            assertEquals(LobbyInformation(lobbyId, emptyList(), "NOT_STARTED"), lobby)
            lobbies.removeIf { it.lobbyId == lobbyId }
        }

        // Only the default lobby is left now
        assertEquals(1, lobbies.size)
        assertEquals(0, lobbies[0].lobbyId)
    }
}
