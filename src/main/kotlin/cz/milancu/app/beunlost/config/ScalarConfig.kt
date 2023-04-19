package cz.milancu.app.beunlost.config

import graphql.kickstart.servlet.apollo.ApolloScalars
import graphql.scalars.ExtendedScalars
import graphql.schema.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant


@Configuration
class ScalarConfig {
    @Bean
    fun uuidScalarType(): GraphQLScalarType {
        return ExtendedScalars.UUID
    }

    @Bean
    fun uploadScalarType(): GraphQLScalarType {
        return ApolloScalars.Upload
    }

    @Bean
    fun jsonScalarType(): GraphQLScalarType? {
        return ExtendedScalars.Json
    }

    @Bean
    fun scalarInstant(): GraphQLScalarType? {
        return GraphQLScalarType.newScalar().name("Instant").description("Standard Java Instant")
            .coercing(object : Coercing<Instant?, String?> {
                @Throws(CoercingSerializeException::class)
                override fun serialize(o: Any): String? {
                    return if (o is Instant) {
                        o.toString()
                    } else {
                        throw CoercingSerializeException("Object is not instance of class Instant!")
                    }
                }

                @Throws(CoercingParseValueException::class)
                override fun parseValue(o: Any): Instant {
                    return try {
                        Instant.parse(o.toString())
                    } catch (e: Exception) {
                        throw CoercingParseLiteralException(e.message, e)
                    }
                }

                @Throws(CoercingParseLiteralException::class)
                override fun parseLiteral(o: Any): Instant {
                    return try {
                        Instant.parse(o.toString())
                    } catch (e: Exception) {
                        throw CoercingParseLiteralException(e.message, e)
                    }
                }
            }).build()
    }
}

