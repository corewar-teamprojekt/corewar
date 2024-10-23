package software.shonk.adapters.incoming

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import software.shonk.module

class ShorkInterpreterControllerTest {

    @Test
    fun testGetStatus() = testApplication {
        application { module() }
        val response = client.get("/api/v0/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testGetStatusDefault() = testApplication {
        application { module() }
        val response = client.get("/api/v0/status")
        assertEquals(
            """
            {
                "playerASubmitted": false,
                "playerBSubmitted": false,
                "gameState": "NOT_STARTED",
                "result": {
                    "winner": "UNDECIDED"
                }
            }
        """
                .trimIndent(),
            response.bodyAsText(),
        )
    }

    @Test
    fun testPostPlayerCodeValidUsername() = testApplication {
        application { module() }
        val player = "playerA"
        val response = client.post("/api/v0/code/$player")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPostPlayerCodeInvalidUsername() = testApplication {
        application { module() }
        val player = "playerC"
        val response = client.post("/api/v0/code/$player")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testPostPlayerCodeValid() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }
        val player = "playerA"
        val response =
            client.post("/api/v0/code/$player") {
                contentType(ContentType.Application.Json)
                setBody("somestring")
            }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPlayerSubmitted() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }
        val player = "playerA"
        client.post("/api/v0/code/$player") {
            contentType(ContentType.Application.Json)
            setBody("somestring")
        }

        val response = client.get("/api/v0/status")
        assertEquals(
            """
            {
                "playerASubmitted": true,
                "playerBSubmitted": false,
                "gameState": "NOT_STARTED",
                "result": {
                    "winner": "UNDECIDED"
                }
            }
        """
                .trimIndent(),
            response.bodyAsText(),
        )
    }

    @Test
    fun testInvalidPlayerSubmitted() = testApplication {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }
        val player = "playerC"
        client.post("/api/v0/code/$player") {
            contentType(ContentType.Application.Json)
            setBody("somestring")
        }

        val response = client.get("/api/v0/status")
        assertEquals(
            """
            {
                "playerASubmitted": false,
                "playerBSubmitted": false,
                "gameState": "NOT_STARTED",
                "result": {
                    "winner": "UNDECIDED"
                }
            }
        """
                .trimIndent(),
            response.bodyAsText(),
        )
    }

    @Test
    fun testBothPlayersSubmittedAndGameStarts() = testApplication {
        application { module() }

        val client = createClient { install(ContentNegotiation) { json() } }
        client.post("/api/v0/code/playerA") {
            contentType(ContentType.Application.Json)
            setBody("someString")
        }

        client.post("/api/v0/code/playerB") {
            contentType(ContentType.Application.Json)
            setBody("someOtherString")
        }

        val response = client.get("/api/v0/status")
        assertNotEquals(
            "NOT_STARTED",
            Json.parseToJsonElement(response.bodyAsText()).jsonObject["gameState"].toString(),
        )
    }
}
