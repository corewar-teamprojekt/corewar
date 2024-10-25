package software.shonk.adapters.incoming

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import software.shonk.adapters.outgoing.shorkInterpreter.MockShorkAdapter
import software.shonk.application.port.incoming.ShorkUseCase
import software.shonk.application.port.outgoing.ShorkPort
import software.shonk.application.service.ShorkService
import software.shonk.module
import software.shonk.moduleApiV0

class ShorkInterpreterControllerTest() : KoinTest {
    private lateinit var testEngine: TestApplicationEngine

    @BeforeTest
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
                single<ShorkPort> { MockShorkAdapter(0) }
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
    fun testBothPlayersSubmittedAndGameStarts() {
        configureCustomDI(
            module {
                single<ShorkPort> { MockShorkAdapter(69) }
                single<ShorkUseCase> { ShorkService(get()) }
            }
        )

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

                val response = client.get("/api/v0/status")
                assertNotEquals(
                    "NOT_STARTED",
                    Json.parseToJsonElement(response.bodyAsText())
                        .jsonObject["gameState"]
                        .toString(),
                )
            }
        }
    }
}
