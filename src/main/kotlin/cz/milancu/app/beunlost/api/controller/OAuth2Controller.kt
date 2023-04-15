package cz.milancu.app.beunlost.api.controller

import cz.milancu.app.beunlost.domain.model.entity.CustomOAuth2User
import cz.milancu.app.beunlost.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.util.*


@Controller
class OAuth2Controller(
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val userService: UserService
) {
    @GetMapping("/loginSuccess")
    fun handleGoogleCallback(@AuthenticationPrincipal oauth2User: OAuth2User?): String? {
        val authentication: OAuth2AuthenticationToken =
            SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val accessToken = (
                authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                    authentication.authorizedClientRegistrationId,
                    authentication.name
                ).accessToken).tokenValue

        val clientId = (
                authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                    authentication.authorizedClientRegistrationId,
                    authentication.name
                ).clientRegistration.clientId)

        val clientSecret = (
                authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                    authentication.authorizedClientRegistrationId,
                    authentication.name
                ).clientRegistration.clientSecret)

        val oauthUser: CustomOAuth2User = authentication.principal as CustomOAuth2User
        userService.createUser(oauthUser)

        return "redirect:http://localhost:3000/auth/?AccessToken=$accessToken"
    }
}