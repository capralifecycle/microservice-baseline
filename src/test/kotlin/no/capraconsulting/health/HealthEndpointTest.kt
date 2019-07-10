package no.capraconsulting.health

import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.builder.ResponseSpecBuilder
import no.capraconsulting.config.AbstractEndpointTest
import org.hamcrest.Matchers
import org.testng.annotations.Test
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Test
class HealthEndpointTest : AbstractEndpointTest() {
    @Test
    fun healthEndpoint_responds_200_without_authentication() {
        val spec = ResponseSpecBuilder()
            .expectStatusCode(Response.Status.OK.statusCode)
            .expectContentType(MediaType.APPLICATION_JSON)
            .expectBody("service", Matchers.notNullValue())
            .build()

        given()
            .get("/health")
            .then()
            .log().ifError()
            .spec(spec)
    }
}
