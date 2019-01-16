package no.capraconsulting.config;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class JsonJettyErrorHandler extends ErrorHandler {
    public JsonJettyErrorHandler() {
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        baseRequest.setHandled(true);
        response.setContentType(MimeTypes.Type.APPLICATION_JSON.asString());
        Writer writer = response.getWriter();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String reason = response instanceof Response ? ((Response)response).getReason() : null;
        this.handleErrorPage(request, writer, response.getStatus(), reason);
        writer.flush();
        response.setContentLength(writer.toString().getBytes().length);
    }

    protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
        if (message == null) {
            message = HttpStatus.getMessage(code);
        }

        writer.write("{ \"code\" : " + code + ", \"message\" : \"" + message + "\" }");
    }
}
