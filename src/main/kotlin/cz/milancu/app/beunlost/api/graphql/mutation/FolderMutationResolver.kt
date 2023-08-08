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

    /**
     * Creates a folder with the given name and labels.
     *
     * @param name    the name of the folder
     * @param labels  the list of labels to associate with the folder
     * @return        the created folder
     */
    fun createFolder(name: String, labels: List<String>): Folder {
        val folder = folderService.createFolder(name)
        schemaService.createSchema(labels, folder.id)

        return folder
    }

    /**
     * Adds a user to a folder by their email.
     *
     * @param email the email of the user to be added
     * @param folderId the ID of the folder to add the user to
     * @return the updated folder after adding the user
     */
    fun addUser(email: String, folderId: UUID): Folder {
        val user = userService.findByEmail(email)
        folderService.addFolderAccess(folderId = folderId, userId = user.id)
        return folderService.findById(folderId)
    }

    /**
     * Updates the schema for a given folder.
     *
     * @param schema the new schema to be updated
     * @param folderId the ID of the folder to update the schema for
     * @return the updated folder
     */
    fun updateSchema(schema: List<String>, folderId: UUID): Folder {
        val folder = folderService.findById(folderId)
        schemaService.updateSchema(schema, folderId)
        return folder
    }

    /**
     * Removes folder access for a user.
     *
     * @param folderId The ID of the folder to remove access from.
     * @param userId The ID of the user to remove access from.
     * @return True if the folder access is successfully removed, false otherwise.
     */
    fun removeFolderAccess(folderId: UUID, userId: UUID): Boolean {
        folderService.removeFolderAccess(folderId, userId)
        return true
    }

    /**
     * Deletes the folder with the given ID.
     *
     * @param folderId The ID of the folder to be deleted.
     * @return True if the folder is successfully deleted, false otherwise.
     */
    fun deleteFolder(folderId: UUID): Boolean {
        folderService.deleteFolder(folderId)
        return true
    }
}