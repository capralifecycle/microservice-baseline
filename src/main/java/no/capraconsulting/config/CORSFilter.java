package no.capraconsulting.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

/**
 * This filter is optionally enabled and wired in {@link JerseyConfig}
 * See {@link JerseyConfig} for details about properties used to control allowed origin and headers
 */
public class CORSFilter implements ContainerResponseFilter {
    private static final Logger log = LoggerFactory.getLogger(CORSFilter.class);

    private final String allowOrigin;
    private final String allowHeaders;
    private final String allowMethods;
    private final String allowCredentials;
    private final String maxAge;

    public CORSFilter(String allowOrigin, String allowHeaders,
                      String allowMethods, String allowCredentials,
                      String maxAge) {
        this.allowOrigin = allowOrigin;
        this.allowHeaders = allowHeaders;
        this.allowMethods = allowMethods;
        this.allowCredentials = allowCredentials;
        this.maxAge = maxAge;
        log.info("Creating CORS filter with the following values:" +
                "Allow-Origin: {}, Allow-Headers: {}, Allow-Methods: {}, Allow-Credentials: {}, Max-Age: {}",
            allowOrigin, allowHeaders, allowMethods, allowCredentials, maxAge);
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", allowOrigin);
        responseContext.getHeaders().add("Access-Control-Allow-Headers", allowHeaders);
        responseContext.getHeaders().add("Access-Control-Allow-Methods", allowMethods);
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", allowCredentials);
        responseContext.getHeaders().add("Access-Control-Max-Age", maxAge);
    }
}
