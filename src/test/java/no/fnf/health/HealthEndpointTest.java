package no.fnf.health;

import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.ResponseSpecification;
import no.fnf.config.AbstractEndpointTest;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.given;

@Test
public class HealthEndpointTest extends AbstractEndpointTest {
    @Test
    public void healthEndpoint_responds_200_without_authentication() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(Response.Status.OK.getStatusCode());
        builder.expectContentType(MediaType.APPLICATION_JSON);
        builder.expectBody("service", Matchers.notNullValue());
        ResponseSpecification spec = builder.build();

        given()
                .get("/health")
                .then()
                .log().ifError()
                .spec(spec);
    }
}
