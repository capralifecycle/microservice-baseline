# microservice-baseline

This project serves as a baseline when you wish to begin a
new micro service exposing HTTP endpoints.

It is set up with the following dependencies/frameworks:
* [Jetty Web Server](https://www.eclipse.org/jetty/)
* [Jersey JAX-RS](https://jersey.github.io/)
* [TestNG](https://github.com/cbeust/testng)
* [REST-assured: Java DSL for easy testing of REST services](https://github.com/rest-assured/rest-assured)
* [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)



### Health endpoint
`HealthEndpoint.java` implements a simple health-endpoint, reachable at /health.r.
The application includes a health-endpoint, reachable at /health.

Example response:
```json
{
  "service": "microservice-baseline",
  "timestamp": "2018-10-15T17:28:00.164Z",
  "runningSince": "2018-10-15T17:27:56.516Z",
  "version": "0.1-SNAPSHOT"
}
```

### Endpoint tests
`HealthEndpointTest.java` showcases how a reusable `TestServer` is started,
and the endpoint tested using REST-assured.

