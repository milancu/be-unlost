package cz.milancu.app.beunlost.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.*

private val log = KotlinLogging.logger { }
private const val TRACE_ID_HEADER = "trace-id"

@Component
class MDCFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var uniqueId: String? = request.getHeader(TRACE_ID_HEADER)
        if (uniqueId.isNullOrEmpty()) {
            uniqueId = UUID.randomUUID().toString()
        }

        MDC.put(TRACE_ID_HEADER, uniqueId)
        val responseWrapper = ContentCachingResponseWrapper(
            response
        )

        filterChain.doFilter(request, responseWrapper)
        responseWrapper.setHeader(TRACE_ID_HEADER, uniqueId)
        responseWrapper.copyBodyToResponse()
        log.info { "Response header is set with uuid ${responseWrapper.getHeader(TRACE_ID_HEADER)}" }
    }
}