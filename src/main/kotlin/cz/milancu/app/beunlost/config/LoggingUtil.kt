package cz.milancu.app.beunlost.config

import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger { }

@Component
class LoggingUtil {

    fun logRequest(request: HttpServletRequest) {
        val parameters: Map<String, String> = getParameters(request)
        val headers = getHeaders(request)
        if (!Objects.isNull(SecurityContextHolder.getContext())) {
            logger.info {
                "${request.method} Request-uri: ${request.requestURI}, Request-headers: $headers, Request-parameters: ${parameters}, By user: ${SecurityContextHolder.getContext().authentication.name}"
            }
        } else {
            logger.info {
                "${request.method} Request-uri: ${request.requestURI}, Request-headers: $headers, Request-parameters: ${parameters}, By user: NOT_AUTHENTICATED"
            }
        }
    }

    fun logResponse(request: HttpServletRequest, response: HttpServletResponse) {
        val headers = getHeaders(response)
        logger.info { "Log Response: Method: ${request.method} Response-Headers = $headers" }
    }

    private fun getHeaders(response: HttpServletResponse): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        val headerMap = response.headerNames
        for (str in headerMap) {
            headers[str] = response.getHeader(str)
        }
        return headers
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        val headerMap = request.headerNames
        for (str in headerMap) {
            headers[str] = request.getHeader(str)
        }
        return headers
    }

    private fun getParameters(request: HttpServletRequest): Map<String, String> {
        val parameters: MutableMap<String, String> = HashMap()
        val params = request.parameterNames
        while (params.hasMoreElements()) {
            val paramName = params.nextElement()
            val paramValue = request.getParameter(paramName)
            parameters[paramName] = paramValue
        }
        return parameters
    }
}