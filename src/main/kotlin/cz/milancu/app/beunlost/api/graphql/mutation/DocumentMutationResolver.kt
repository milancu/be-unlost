package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.domain.model.entity.AttributeKeyValueModel
import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.service.DocumentService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import org.apache.catalina.core.ApplicationPart
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*


/**
 * This class is responsible for handling mutations related to documents.
 *
 * @property documentService The service to interact with documents.
 */
@Component
class DocumentMutationResolver(
    private val documentService: DocumentService,
) : GraphQLMutationResolver {
    /**
     * Uploads images to a specified folder.
     *
     * @param parts the list of ApplicationPart objects representing the images to be uploaded
     * @param folderId the ID of the folder to upload the images to
     * @param env the DataFetchingEnvironment object for accessing additional data
     * @return true if the images are successfully uploaded, otherwise false
     * @throws IOException if there is an error during the upload process
     */
    @Throws(IOException::class)
    fun uploadImage(parts: List<ApplicationPart>, folderId: String?, env: DataFetchingEnvironment): Boolean {
        val files: List<ApplicationPart> = env.getArgument("files")
        files.stream().forEach { documentService.uploadDocument(file = it, folderId = UUID.fromString(folderId)) }
        return true
    }

    /**
     * Updates the annotations for a specific document.
     *
     * @param annotations a list of AttributeKeyValueModel representing the annotations to update
     * @param documentId the UUID identifier of the document to update the annotations for
     * @return true if the annotations were successfully updated, false otherwise
     */
    fun updateAnnotation(annotations: List<AttributeKeyValueModel>, documentId: UUID): Boolean {
        documentService.updateAnnotation(annotations = annotations, documentId = documentId)
        return true
    }

    /**
     * Deletes a document specified by its documentId.
     *
     * @param documentId The unique identifier of the document to be deleted.
     * @return Returns `true` if the document is successfully deleted, otherwise `false`.
     */
    fun deleteDocument(documentId: UUID): Boolean {
        documentService.deleteDocument(documentId)
        return true
    }

    /**
     * Renames a document with the given document ID to the specified new filename.
     *
     * @param documentId The UUID of the document to be renamed.
     * @param newFilename The new filename for the document.
     * @return The updated Document object after renaming.
     */
    fun renameDocument(documentId: UUID, newFilename: String): Document {
        return documentService.renameDocument(documentId, newFilename)
    }

    /**
     * Locks the document with the specified document ID.
     * This method will call the `lockDocument` method of the `documentService` to lock the document.
     *
     * @param documentId The unique identifier of the document to be locked.
     *
     * @return The locked Document instance.
     */
    fun lockDocument(documentId: UUID): Document {
        return documentService.lockDocument(documentId)
    }

    /**
     * Unlocks the document with the specified document ID.
     *
     * @param documentId the ID of the document to unlock
     * @return the unlocked document
     */
    fun unlockDocument(documentId: UUID): Document {
        return documentService.unlockDocument(documentId)
    }

    /**
     * Adds document access for a given document ID and email.
     *
     * @param documentId The ID of the document to grant access to.
     * @param email The email address of the user to grant access to the document.
     * @return The updated Document object with the newly added access.
     */
    fun addDocumentAccess(documentId: UUID, email: String): Document {
        return documentService.addDocumentAccess(documentId = documentId, email = email)
    }
}

