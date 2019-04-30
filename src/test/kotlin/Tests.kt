package com.epam.drill

import com.sun.net.httpserver.HttpServer
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.net.InetSocketAddress
import kotlin.test.assertEquals

const val headerName = "DrillTestName"

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

        @BeforeClass
        @JvmStatic
        fun startSimpleEchoServer() {
            httpServer.start()
        }

        @AfterClass
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
}