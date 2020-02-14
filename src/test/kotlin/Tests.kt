package com.epam.drill

import com.sun.net.httpserver.HttpServer
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

const val headerName = "drill-test-name"

class Tests {

    companion object {

        var httpServer: HttpServer = HttpServer.create(InetSocketAddress(0), 0)
        var port: Int


        init {
            httpServer.createContext("/echo") { t ->
                val response = "OK"
                t.responseHeaders.add(headerName, t.requestHeaders.getFirst(headerName) ?: "empty")
                t.sendResponseHeaders(200, response.toByteArray().size.toLong())
                val os = t.responseBody
                os.write(response.toByteArray())
                os.close()
            }
            httpServer.executor = null
            port = httpServer.address.port
        }

        @BeforeAll
        @JvmStatic
        fun startSimpleEchoServer() {
            httpServer.start()
        }

        @AfterAll
        @JvmStatic
        fun destroySimpleEchoServer() {
            httpServer.stop(1)
        }
    }

    @Test
    fun simpleTestMethodName() {
        test(::simpleTestMethodName.name)
    }


    @Test
    fun `method with backtick names`() {
        test(::`method with backtick names`.name)
    }

    @Suppress("RemoveRedundantBackticks")
    @Test
    fun `shortBacktick`() {
        test(::`shortBacktick`.name)
    }

    private fun test(methodName: String) {
        val client = HttpClients.createDefault()!!
        val request = HttpPost("http://localhost:$port/echo")
        val response = client.execute(request)
        assertEquals(methodName, response.getHeaders(headerName)[0].value)
    }

    @Test
    fun `should take sessionId for one agent`() {
        val sessionId = "6c6ccb1c-c33f-4896-abe8-d53912ae5032"
        val jsonResponseFromAdmin = "{\"code\": 200,\"data\":{\"payload\": {\"sessionId\": \"$sessionId\",\"startPayload\":{\"testType\": \"MANUAL\",\"sessionId\": \"6c6ccb1c-c33f-4896-abe8-d53912ae5032\"}}}}"
        assertEquals(sessionId, DrillCoverageTestAgent.getSessionId(jsonResponseFromAdmin, true))
    }

    @Test
    fun `should take sessionId for service group`() {
        val sessionId = "6c6ccb1c-c33f-4896-abe8-d53912ae5032"
        val jsonResponseFromAdmin = "[{\"code\": 200,\"data\":{\"payload\": {\"sessionId\": \"$sessionId\",\"startPayload\":{\"testType\": \"MANUAL\",\"sessionId\": \"6c6ccb1c-c33f-4896-abe8-d53912ae5032\"}}}}]"
        assertEquals(sessionId, DrillCoverageTestAgent.getSessionId(jsonResponseFromAdmin, false))
    }
}
