package no.fnf.secured;

import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.ResponseSpecification;
import no.fnf.config.AbstractEndpointTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.given;

@Test
public class ProtectedEndpointTest extends AbstractEndpointTest {

    @Test
    public void securedEndpoint_responds_401_without_authentication() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(Response.Status.UNAUTHORIZED.getStatusCode());
        builder.expectContentType(MediaType.APPLICATION_JSON);
        ResponseSpecification spec = builder.build();

        given()
                .get(ProtectedEndpoint.SECURED_PATH)
                .then()
                .log().ifError()
                .spec(spec);
    }

    @Test
    public void securedEndpoint_responds_401_with_incorrect_password() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(Response.Status.UNAUTHORIZED.getStatusCode());
        builder.expectContentType(MediaType.APPLICATION_JSON);
        ResponseSpecification spec = builder.build();

        given()
                .when()
                .auth()
                .basic("usernameTest", "incorrectPW")
                .get(ProtectedEndpoint.SECURED_PATH)
                .then()
                .log().ifError()
                .spec(spec);
    }

    @Test
    public void securedEndpoint_responds_200_with_authentication() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(Response.Status.OK.getStatusCode());
        builder.expectContentType(MediaType.APPLICATION_JSON);
        ResponseSpecification spec = builder.build();

        given()
                .when()
                .auth()
                .basic("usernameTest", "passwordTest")
                .get(ProtectedEndpoint.SECURED_PATH)
                .then()
                .log().ifError()
                .spec(spec);
    }
}
