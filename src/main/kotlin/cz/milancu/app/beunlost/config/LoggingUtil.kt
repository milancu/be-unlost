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

    /**
     * Logs the incoming HTTP request.
     *
     * @param request the HttpServletRequest object representing the incoming request.
     */
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

    /**
     * Logs the response details.
     *
     * @param request The HTTP servlet request object.
     * @param response The HTTP servlet response object.
     */
    fun logResponse(request: HttpServletRequest, response: HttpServletResponse) {
        val headers = getHeaders(response)
        logger.info { "Log Response: Method: ${request.method} Response-Headers = $headers" }
    }

    /**
     * Retrieves the headers from the provided HttpServletResponse object.
     *
     * @param response the HttpServletResponse object from which to retrieve the headers
     * @return a map containing the headers, where the key is the header name and the value is the header value
     */
    private fun getHeaders(response: HttpServletResponse): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        val headerMap = response.headerNames
        for (str in headerMap) {
            headers[str] = response.getHeader(str)
        }
        return headers
    }

    /**
     * Retrieves the headers from the given HttpServletRequest.
     *
     * @param request The HttpServletRequest object from which to retrieve the headers.
     * @return A map containing the headers, where the keys are the header names and the values are the header values.
     */
    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        val headerMap = request.headerNames
        for (str in headerMap) {
            headers[str] = request.getHeader(str)
        }
        return headers
    }

    /**
     * Gets the parameters from the given HttpServletRequest.
     *
     * @param request the HttpServletRequest object from which the parameters should be retrieved
     * @return a map containing the parameter names and values as key-value pairs
     */
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