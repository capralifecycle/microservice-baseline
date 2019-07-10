package no.capraconsulting.secured

import com.jayway.restassured.RestAssured.given
import com.jayway.restassured.builder.ResponseSpecBuilder
import no.capraconsulting.config.AbstractEndpointTest
import org.testng.annotations.Test
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Test
class ProtectedEndpointTest : AbstractEndpointTest() {

    @Test
    fun securedEndpoint_responds_401_without_authentication() {
        val spec = ResponseSpecBuilder()
            .expectStatusCode(Response.Status.UNAUTHORIZED.statusCode)
            .expectContentType(MediaType.APPLICATION_JSON)
            .build()

        given()
            .get(ProtectedEndpoint.SECURED_PATH)
            .then()
            .log().ifError()
            .spec(spec)
    }

    @Test
    fun securedEndpoint_responds_401_with_incorrect_password() {
        given()
            .`when`()
            .auth()
            .basic("usernameTest", "incorrectPW")
            .get(ProtectedEndpoint.SECURED_PATH)
            .then()
            .log().ifError()
            .spec(
                ResponseSpecBuilder()
                    .expectStatusCode(Response.Status.UNAUTHORIZED.statusCode)
                    .expectContentType(MediaType.APPLICATION_JSON)
                    .build()
            )
    }

    @Test
    fun securedEndpoint_responds_200_with_authentication() {
        given()
            .`when`()
            .auth()
            .basic("usernameTest", "passwordTest")
            .get(ProtectedEndpoint.SECURED_PATH)
            .then()
            .log().ifError()
            .spec(
                ResponseSpecBuilder()
                    .expectStatusCode(Response.Status.OK.statusCode)
                    .expectContentType(MediaType.APPLICATION_JSON)
                    .build()
            )
    }
}
