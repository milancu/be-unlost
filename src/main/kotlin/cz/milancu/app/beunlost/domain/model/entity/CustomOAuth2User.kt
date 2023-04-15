package cz.milancu.app.beunlost.domain.model.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val oAuth2User: OAuth2User
) : OAuth2User {
    override fun getName(): String {
        return oAuth2User.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return oAuth2User.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return oAuth2User.authorities
    }

    fun getFirstname(): String {
        oAuth2User.attributes.forEach { (t, u )-> println("$t: $u")}
        return oAuth2User.attributes.getValue("given_name").toString();
    }

    fun getLastname(): String {
        return oAuth2User.attributes.getValue("family_name").toString();
    }

    fun getGivenName(): String {
        return oAuth2User.attributes.getValue("given_name").toString();
    }

    fun getEmail(): String {
        return oAuth2User.attributes.getValue("email").toString();
    }

    fun getImageUrl(): String {
        return oAuth2User.attributes.getValue("picture").toString();
    }
}