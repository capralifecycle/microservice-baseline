package no.capraconsulting.config

import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.http.MimeTypes
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.server.handler.ErrorHandler
import java.io.Writer
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JsonJettyErrorHandler : ErrorHandler() {

    override fun handle(
        target: String?,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        baseRequest.isHandled = true
        response.contentType = MimeTypes.Type.APPLICATION_JSON.asString()
        val writer = response.writer
        response.characterEncoding = StandardCharsets.UTF_8.name()
        val reason = if (response is Response) response.reason else null
        handleErrorPage(request, writer, response.status, reason)
        writer.flush()
        response.setContentLength(writer.toString().toByteArray().size)
    }

    override fun writeErrorPage(
        request: HttpServletRequest,
        writer: Writer,
        code: Int,
        message: String?,
        showStacks: Boolean
    ) {
        val message1 = message ?: HttpStatus.getMessage(code)
        writer.write("{ \"code\" : $code, \"message\" : \"$message1\" }")
    }
}
