package software.shonk.adapters.incoming

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals
import software.shonk.module

class ShorkInterpreterControllerTest {

    @Test
    fun testGetStatus() = testApplication {
        application { module() }
        val response = client.get("/api/v0/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
