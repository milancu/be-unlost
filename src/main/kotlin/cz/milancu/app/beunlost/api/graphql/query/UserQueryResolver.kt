package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.User
import cz.milancu.app.beunlost.service.UserService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserQueryResolver(
    private val userService: UserService
) : GraphQLQueryResolver {

    fun getCurrentUser(): User {
        return userService.getCurrentUser()
    }

    fun getUser(id: UUID):User{
        return userService.findById(id)
    }
}