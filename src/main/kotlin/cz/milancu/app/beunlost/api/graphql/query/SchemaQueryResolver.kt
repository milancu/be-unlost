package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.CustomSchema
import cz.milancu.app.beunlost.service.SchemaService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class SchemaQueryResolver(
    private val schemaService: SchemaService
):GraphQLQueryResolver {

    fun getSchema(id: UUID):CustomSchema{
        return schemaService.findById(id)
    }
}