package no.capraconsulting

import com.jayway.restassured.RestAssured
import org.slf4j.LoggerFactory
import java.util.Properties

class TestServer(private val port: Int, private val properties: Properties) {
    private var main: AppMain? = null

    fun start() {
        Thread {
            main = AppMain(port, properties)
            try {
                main?.start()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }.start()
        do {
            Thread.sleep(10)
        } while (main?.isStarted() != true)
        RestAssured.port = main!!.port
        log.info("Starting TestServer on Port:$port")
        RestAssured.basePath = AppMain.CONTEXT_PATH
    }

    fun stop() {
        main?.stop()
    }

    companion object {
        private val log = LoggerFactory.getLogger(TestServer::class.java)
    }
}
