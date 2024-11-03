package software.shonk.adapters.incoming

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.junit5.JUnit5Asserter.assertNotEquals
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.service.ShorkService
import software.shonk.interpreter.IShork
import software.shonk.interpreter.MockShork
import software.shonk.module
import software.shonk.moduleApiV0

class ShorkInterpreterControllerIT() : KoinTest {
    private lateinit var testEngine: TestApplicationEngine

    @BeforeEach
    fun beforeTest() {
        testEngine =
            TestApplicationEngine(
                createTestEnvironment {
                    config =
                        MapApplicationConfig(
                            "ktor.environment" to "test",
                            "ktor.deployments.port" to "8080",
                        )
                }
            )

        testEngine.start(wait = false)
        testEngine.application.apply {
            module()
            moduleApiV0()
        }
        configureCustomDI(
            module {
                single<IShork> { MockShork() }
                single<ShorkUseCase> { ShorkService(get()) }
            }
        )
    }

    private fun configureCustomDI(module: Module) {
        testEngine.application.apply {
            stopKoin()
            startKoin { modules(module) }
        }
    }

    @Test
    fun testGetStatus() =
        with(testEngine) {
            runBlocking {
                val response = client.get("/api/v0/status")
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

    @Test
    fun testGetStatusDefault() =
        with(testEngine) {
            runBlocking {
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
        }

    @Test
    fun testPostPlayerCodeValidUsername() =
        with(testEngine) {
            runBlocking {
                val player = "playerA"
                val response = client.post("/api/v0/code/$player")
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

    @Test
    fun testPostPlayerCodeInvalidUsername() =
        with(testEngine) {
            runBlocking {
                val player = "playerC"
                val response = client.post("/api/v0/code/$player")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }

    @Test
    fun testPostPlayerCodeValid() =
        with(testEngine) {
            runBlocking {
                val player = "playerA"
                val response =
                    client.post("/api/v0/code/$player") {
                        contentType(ContentType.Application.Json)
                        setBody("somestring")
                    }
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

    @Test
    fun testPlayerSubmitted() =
        with(testEngine) {
            runBlocking {
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
        }

    @Test
    fun testInvalidPlayerSubmitted() =
        with(testEngine) {
            runBlocking {
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
        }

    @Test
    fun testBothPlayersSubmittedAndGameStartsAndLongerProgramWins() {
        with(testEngine) {
            runBlocking {
                client.post("/api/v0/code/playerA") {
                    contentType(ContentType.Application.Json)
                    setBody("looooooooong")
                }

                client.post("/api/v0/code/playerB") {
                    contentType(ContentType.Application.Json)
                    setBody("short")
                }

                val response = client.get("/api/v0/status")

                assertNotEquals(
                    "wrong gamestate, should be anything, but NOT_STARTED",
                    "NOT_STARTED",
                    Json.parseToJsonElement(response.bodyAsText())
                        .jsonObject["gameState"]
                        .toString(),
                )
                assertEquals(
                    "A",
                    Json.parseToJsonElement(response.bodyAsText())
                        .jsonObject["result"]
                        ?.jsonObject
                        ?.get("winner")
                        ?.jsonPrimitive
                        ?.content,
                )
            }
        }
    }

    @Test
    fun testBothPlayersSubmittedGameGetsSimulatedPlayerCodeAndFlagsResetAfterNewPlayerCodeGetsSubmitted() {
        with(testEngine) {
            runBlocking {
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
        }
    }
}
