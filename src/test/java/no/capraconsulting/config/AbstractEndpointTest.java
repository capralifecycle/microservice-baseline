package no.capraconsulting.config;

import no.capraconsulting.TestServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.Random;

public abstract class AbstractEndpointTest {
    private static TestServer testServer;
    private static int port;
    private static final Random random = new Random(System.currentTimeMillis());

    @BeforeSuite(alwaysRun = true, timeOut = 10000L)
    public void startTestServer() throws Exception {
        port = findAvailableTcpPort();
        testServer = new TestServer(port, getTestServerProperties());
        testServer.start();
    }

    @AfterSuite(alwaysRun = true)
    public void stop() {
        testServer.stop();
    }

    private static Properties getTestServerProperties() {
        Properties properties = new Properties();
        properties.putAll(PropertiesHelper.getPropertiesFromClasspathFile("application.properties"));
        properties.putAll(PropertiesHelper.getPropertiesFromClasspathFile("application-test.properties"));
        return properties;
    }

    // Functionality retrieved from https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/SocketUtils.java
    private static int findAvailableTcpPort() {
        int minPort = 1024;
        int maxPort = 65535;
        int portRange = maxPort - minPort;
        int candidatePort;
        int searchCounter = 0;
        do {
            if (searchCounter > portRange) {
                throw new IllegalStateException(String.format(
                        "Could not find an available port in the range [%d, %d] after %d attempts",
                        minPort, maxPort, searchCounter));
            }
            candidatePort = findRandomPort(minPort, maxPort);
            searchCounter++;
        }
        while (!isPortAvailable(candidatePort));

        return candidatePort;
    }

    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                    port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private static int findRandomPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        return minPort + random.nextInt(portRange + 1);
    }
}
