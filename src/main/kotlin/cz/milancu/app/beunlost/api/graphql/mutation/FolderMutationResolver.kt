package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.SchemaService
import cz.milancu.app.beunlost.service.UserService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FolderMutationResolver(
    private val folderService: FolderService,
    private val schemaService: SchemaService,
    private val userService: UserService
) : GraphQLMutationResolver {

    fun createFolder(name: String, labels: List<String>): Folder {
        val folder = folderService.createFolder(name)
        schemaService.createSchema(labels, folder.id)

        return folder
    }

    fun addUser(email: String, folderId: UUID): Folder {
        val user = userService.findByEmail(email)
        folderService.addFolderAccess(folderId = folderId, userId = user.id)
        return folderService.findById(folderId)
    }

    fun updateSchema(schema: List<String>, folderId: UUID): Folder {
        val folder = folderService.findById(folderId)
        schemaService.updateSchema(schema, folderId)
        return folder
    }
}