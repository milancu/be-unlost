package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.service.UserService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component

@Component
class UserMutationResolver(
    private val userService: UserService
) : GraphQLMutationResolver {
    fun createUser(): Boolean {
        return true
    }
}