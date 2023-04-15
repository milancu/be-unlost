package cz.milancu.app.beunlost.config.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class GoogleOAuth2AuthenticationToken(
    val email: String,
    private val authorities: MutableCollection<out GrantedAuthority>?
) :
    AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): Any {
        return email;
    }

}