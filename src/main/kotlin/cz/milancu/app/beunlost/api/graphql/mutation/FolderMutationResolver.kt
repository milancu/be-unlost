package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.SchemaService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component

@Component
class FolderMutationResolver(
    private val folderService: FolderService,
    private val schemaService: SchemaService
) : GraphQLMutationResolver {

    fun createFolder(name: String, labels: List<String>): Boolean {
        val folder = folderService.createFolder(name)

        if (labels.isNotEmpty()) {
            schemaService.createSchema(labels, folder.id)
        }

        return true
    }
}