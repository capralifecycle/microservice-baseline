package no.capraconsulting.config

import no.capraconsulting.TestServer
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeSuite
import java.net.InetAddress
import java.util.Properties
import javax.net.ServerSocketFactory

abstract class AbstractEndpointTest {

    @BeforeSuite(alwaysRun = true, timeOut = 10000L)
    fun startTestServer() {
        port = findAvailableTcpPort()
        testServer = TestServer(port, testServerProperties)
        testServer.start()
    }

    @AfterSuite(alwaysRun = true)
    fun stop() {
        testServer.stop()
    }

    companion object {
        private lateinit var testServer: TestServer
        private var port: Int = 0

        private val testServerProperties: Properties
            get() = Properties().apply {
                putAll(PropertiesHelper.getPropertiesFromClasspathFile("application.properties"))
                putAll(PropertiesHelper.getPropertiesFromClasspathFile("application-test.properties"))
            }

        // Functionality retrieved from https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/SocketUtils.java
        private fun findAvailableTcpPort(): Int {
            val ports = 1024..65535
            val count = ports.last - ports.first + 1

            repeat(count) {
                val candidatePort = ports.random()
                if (isPortAvailable(candidatePort)) return candidatePort
            }

            throw IllegalStateException(
                "Could not find an available port in the range [%d, %d] after %d attempts".format(
                    ports.first, ports.last, count
                )
            )
        }

        private fun isPortAvailable(port: Int): Boolean {
            return try {
                val serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                    port, 1, InetAddress.getByName("localhost")
                )
                serverSocket.close()
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}
