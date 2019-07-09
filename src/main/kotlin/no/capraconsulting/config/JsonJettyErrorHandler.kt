package no.capraconsulting.config

import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.http.MimeTypes
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.server.handler.ErrorHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.Writer
import java.nio.charset.StandardCharsets

class JsonJettyErrorHandler : ErrorHandler() {

    @Throws(IOException::class)
    override fun handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        baseRequest.isHandled = true
        response.contentType = MimeTypes.Type.APPLICATION_JSON.asString()
        val writer = response.writer
        response.characterEncoding = StandardCharsets.UTF_8.name()
        val reason = if (response is Response) response.reason else null
        this.handleErrorPage(request, writer, response.status, reason)
        writer.flush()
        response.setContentLength(writer.toString().toByteArray().size)
    }

    @Throws(IOException::class)
    override fun writeErrorPage(
        request: HttpServletRequest,
        writer: Writer,
        code: Int,
        message: String?,
        showStacks: Boolean
    ) {
        var message = message
        if (message == null) {
            message = HttpStatus.getMessage(code)
        }

        writer.write("{ \"code\" : $code, \"message\" : \"$message\" }")
    }
}
