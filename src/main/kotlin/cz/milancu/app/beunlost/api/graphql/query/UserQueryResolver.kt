package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.User
import cz.milancu.app.beunlost.service.UserService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class UserQueryResolver(
    private val userService: UserService
) : GraphQLQueryResolver {
    private val GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo"


    fun getCurrentUser(): User {
        return userService.getCurrentUser()
    }

    fun getUser(id: UUID): User {
        return userService.findById(id)
    }
}