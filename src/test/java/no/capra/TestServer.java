package no.capra;

import com.jayway.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {
    private static final Logger log = LoggerFactory.getLogger(TestServer.class);

    private final int port;
    private AppMain main;


    public TestServer(int port) {
        this.port = port;

    }

    public void start() throws Exception {
        new Thread(() -> {
            main = new AppMain(port);
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
