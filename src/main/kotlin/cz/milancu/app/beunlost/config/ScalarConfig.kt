package cz.milancu.app.beunlost.config

import graphql.kickstart.servlet.apollo.ApolloScalars
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ScalarConfig {
    @Bean
    fun uuidScalarType(): GraphQLScalarType {
        return ExtendedScalars.UUID
    }

    @Bean
    fun uploadScalarType(): GraphQLScalarType? {
        return ApolloScalars.Upload
    }

    @Bean
    fun instantScalarType(): GraphQLScalarType? {
        return ExtendedScalars.DateTime
    }
}

