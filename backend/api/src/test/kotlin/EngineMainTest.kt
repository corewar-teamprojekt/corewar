package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import software.shonk.module

class ApplicationTest {
    @Test
    fun testHello() = testApplication {
        application { module() }
        val response = client.get("/api/hello")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello world!", response.bodyAsText())
    }
}
