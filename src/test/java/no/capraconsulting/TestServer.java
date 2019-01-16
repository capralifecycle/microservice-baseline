package no.capraconsulting;

import com.jayway.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TestServer {
    private static final Logger log = LoggerFactory.getLogger(TestServer.class);

    private final int port;
    private final Properties properties;
    private AppMain main;


    public TestServer(int port, Properties properties) {
        this.port = port;
        this.properties = properties;
    }

    public void start() throws Exception {
        new Thread(() -> {
            main = new AppMain(port, properties);
            try {
                main.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        do {
            Thread.sleep(10);
        } while (main == null || !main.isStarted());
        RestAssured.port = main.getPort();
        log.info("Starting TestServer on Port:" + port);
        RestAssured.basePath = AppMain.CONTEXT_PATH;
    }

    public void stop() {
        main.stop();
    }

    public int getPort() {
        return this.port;
    }
}
