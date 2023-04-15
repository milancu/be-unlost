package cz.milancu.app.beunlost.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*


@Component
class GoogleOAuth2Filter(
) : OncePerRequestFilter() {

    private val GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo"

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val accessToken = authorizationHeader.substring(7)
            try {
                val email = validateAccessToken(accessToken)!!
                val oauth2User = GoogleOAuth2AuthenticationToken(
                    email = email,
                    authorities = Collections.singleton(SimpleGrantedAuthority("user"))
                )
                val authentication = UsernamePasswordAuthenticationToken(
                    oauth2User,
                    null,
                    oauth2User.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: GeneralSecurityException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.message)
            } catch (e: GoogleJsonResponseException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.message)
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun validateAccessToken(accessToken: String): String? {
        val restTemplate = RestTemplate()
        val url = "$GOOGLE_TOKEN_INFO_URL?access_token=$accessToken"
        val responseEntity = restTemplate.getForEntity(url, String::class.java)
        return extractEmailFromJson(responseEntity.body!!)
    }

    private fun extractEmailFromJson(jsonString: String): String? {
        val objectMapper = ObjectMapper()
        return try {
            val tokenResponse = objectMapper.readValue(jsonString, AccessTokenResponse::class.java)
            tokenResponse.email
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}


data class AccessTokenResponse(
    var azp: String? = null,
    var aud: String? = null,
    var sub: String? = null,
    var scope: String? = null,
    var exp: String? = null,
    var expires_in: String? = null,
    var email: String? = null,
    var email_verified: String? = null,
    var access_type: String? = null
) {
    constructor() : this(null, null, null, null, null, null, null, null, null)
}
