package software.shonk.adapters.incoming

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import software.shonk.domain.CompileError
import software.shonk.domain.LobbyStatus
import software.shonk.module
import software.shonk.moduleApiV1

class ShorkInterpreterControllerV1IT : AbstractControllerTest() {

    override fun applyTestEngineApplication() {
        testEngine.application.apply {
            module()
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

    private suspend fun parseAllLobbies(response: HttpResponse): List<LobbyStatus> {
        val responseJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val lobbiesArray = responseJson["lobbies"]!!.jsonArray
        return lobbiesArray.map { Json.decodeFromJsonElement<LobbyStatus>(it) }
    }

    @Test
    fun `test post and get player code`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
        val result = client.get("/api/v1/lobby/status/0")
        val responseData = parseStatus(result)
        assertEquals("false", responseData["playerASubmitted"])
        assertEquals("false", responseData["playerBSubmitted"])
        assertEquals("NOT_STARTED", responseData["gameState"])
        assertEquals("UNDECIDED", responseData["result.winner"])
    }

    @Test
    fun `test get lobby status with valid custom ID`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
        val player = "playerA"
        val result = client.post("/api/v1/lobby/0/code/$player")
        assertEquals(HttpStatusCode.OK, result.status)
    }

    @Test
    fun `test player code submission with invalid username does not change lobby status`() =
        runTest {
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
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
    fun `test create a new lobby with playerName`() = runTest {
        val result =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
        assertEquals(HttpStatusCode.Created, result.status)
        val responseId =
            Json.parseToJsonElement(result.bodyAsText())
                .jsonObject["lobbyId"]!!
                .jsonPrimitive
                .content
        assertEquals("0", responseId)
    }

    @Test
    fun `test create a multiple new lobbies`() = runTest {
        val result =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
        val result2 =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerB\"}")
            }
        val result3 =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerC\"}")
            }
        assertEquals(HttpStatusCode.Created, result.status)
        val responseId0 =
            Json.parseToJsonElement(result.bodyAsText())
                .jsonObject["lobbyId"]!!
                .jsonPrimitive
                .content
        assertEquals("0", responseId0)
        assertEquals(HttpStatusCode.Created, result2.status)
        val responseId1 =
            Json.parseToJsonElement(result2.bodyAsText())
                .jsonObject["lobbyId"]!!
                .jsonPrimitive
                .content
        assertEquals("1", responseId1)
        assertEquals(HttpStatusCode.Created, result3.status)
        val responseId2 =
            Json.parseToJsonElement(result3.bodyAsText())
                .jsonObject["lobbyId"]!!
                .jsonPrimitive
                .content
        assertEquals("2", responseId2)
    }

    @Test
    fun `test create a new lobby with invalid playerName`() = runTest {
        val result =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"\"}")
            }
        assertEquals(HttpStatusCode.BadRequest, result.status)
        assertEquals("Your player name is invalid", result.bodyAsText())
    }

    @Test
    fun `test body with invalid json`() = runTest {
        val result =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{ invalid, : json :3")
            }
        assertEquals(HttpStatusCode.BadRequest, result.status)
    }

    @Test
    fun `test body with playername null`() = runTest {
        val result =
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{ }")
            }

        assertEquals(HttpStatusCode.BadRequest, result.status)
    }

    @Test
    fun `test join lobby with valid playerName`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
        val result =
            client.post("/api/v1/lobby/0/join") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerB\"}")
            }
        assertEquals(HttpStatusCode.OK, result.status)
    }

    @Test
    fun `test join lobby with duplicate (invalid) playerName`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
        val result =
            client.post("/api/v1/lobby/0/join") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
        assertEquals(HttpStatusCode.Conflict, result.status)
    }

    @Test
    fun `test join lobby with invalid json`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }
        val result =
            client.post("/api/v1/lobby/0/join") {
                contentType(ContentType.Application.Json)
                setBody("{ invalid, : json :3")
            }
        assertEquals(HttpStatusCode.BadRequest, result.status)
    }

    @Test
    fun `test join nonexistent lobby`() = runTest {
        val result =
            client.post("/api/v1/lobby/0/join") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
        assertEquals(HttpStatusCode.NotFound, result.status)
    }

    @Test
    fun `test get all lobbies when there is only one and it's not initialized`() = runTest {
        client.post("/api/v1/lobby") {
            contentType(ContentType.Application.Json)
            setBody("{\"playerName\":\"playerA\"}")
        }

        val result = client.get("/api/v1/lobby")
        val parsedLobbies = parseAllLobbies(result)

        assertEquals(HttpStatusCode.OK, result.status)
        assertEquals(1, parsedLobbies.size)

        assertTrue(
            parsedLobbies.contains(
                LobbyStatus(id = 0L, playersJoined = listOf("playerA"), gameState = "NOT_STARTED")
            )
        )
    }

    @Test
    fun `test get all lobbies as a list with players and games status for more than one lobby`() =
        runTest {
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }
            client.post("/api/v1/lobby") {
                contentType(ContentType.Application.Json)
                setBody("{\"playerName\":\"playerA\"}")
            }

            val result = client.get("/api/v1/lobby")
            val parsedLobbies = parseAllLobbies(result)

            assertEquals(HttpStatusCode.OK, result.status)
            assertEquals(3, parsedLobbies.size)

            assertTrue(
                parsedLobbies.contains(
                    LobbyStatus(
                        id = 0L,
                        playersJoined = listOf("playerA"),
                        gameState = "NOT_STARTED",
                    )
                )
            )

            assertTrue(
                parsedLobbies.contains(
                    LobbyStatus(
                        id = 1L,
                        playersJoined = listOf("playerA"),
                        gameState = "NOT_STARTED",
                    )
                )
            )

            assertTrue(
                parsedLobbies.contains(
                    LobbyStatus(
                        id = 2L,
                        playersJoined = listOf("playerA"),
                        gameState = "NOT_STARTED",
                    )
                )
            )
        }

    @Serializable data class Program(val code: String)

    // Workaround for content negotiation plugin bullshit
    fun Program.json() = Json.encodeToString(Program.serializer(), this)

    @Serializable data class CompileErrorResponse(val errors: List<CompileError>)

    @Test
    fun `test compile invalid json`() = runTest {
        val result =
            client.post("/api/v1/redcode/compile/errors") {
                contentType(ContentType.Application.Json)
                setBody("{ invalid, : json >:3c")
            }

        assertEquals(HttpStatusCode.BadRequest, result.status)
    }

    @Test
    fun `test compile no errors`() = runTest {
        val result =
            client.post("/api/v1/redcode/compile/errors") {
                contentType(ContentType.Application.Json)
                setBody(Program("MOV 0, 1").json())
            }

        assertEquals(HttpStatusCode.OK, result.status)
    }

    @Test
    fun `test compile with errors`() = runTest {
        val result =
            client.post("/api/v1/redcode/compile/errors") {
                contentType(ContentType.Application.Json)
                setBody(Program("ASduhsdlfuhsdf dlfasuihfals Totally valid code :333").json())
            }

        assertEquals(HttpStatusCode.OK, result.status)
        val response = Json.decodeFromString(CompileErrorResponse.serializer(), result.bodyAsText())
        assertTrue(response.errors.isNotEmpty())
    }
}
