package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.*
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
import cz.milancu.app.beunlost.domain.repository.UserRepository
import cz.milancu.app.beunlost.service.*
import mu.KotlinLogging
import org.apache.catalina.core.ApplicationPart
import org.springframework.data.elasticsearch.client.elc.QueryBuilders
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.streams.toList


private val log = KotlinLogging.logger { }

@Service
class DocumentServiceImpl(
    private val documentRepository: DocumentRepository,
    private val documentAccessService: DocumentAccessService,
    private val folderService: FolderService,
    private val userService: UserService,
    private val visionUtil: GCPVisionUtil,
    private val bucketUtil: GCPBucketUtil,
    private val userRepository: UserRepository,
    private val elasticsearchOperations: ElasticsearchOperations
) : DocumentService {
    /**
     * Uploads a document.
     *
     * @param file The file to be uploaded as an ApplicationPart.
     * @param folderId The optional folder ID where the document should be stored. If null, the document will not be associated with any folder.
     */
    override fun uploadDocument(file: ApplicationPart, folderId: UUID?) {
        val currentUser = userService.getCurrentUser()
        val document = Document(
            filename = file.submittedFileName,
            createByUser = currentUser.id,
            documentStatus = DocumentStatus.UPLOADING,
            folderId = folderId
        )
        documentRepository.save(document)
        log.info { "Saved entity document to database with id:${document.id}, documentState: ${document.documentStatus}" }
        if (folderId != null) {
            folderService.addDocument(folderId, document)
            log.info { "Add document ${document.id} to folder with id:${folderId}" }
        }

        bucketUtil.uploadFileToBucketAndSave(file = file, documentId = document.id)
        visionUtil.extractTextAndSave(file = file, documentId = document.id)

//        addDocumentAccess(documentId = document.id, email = currentUser.email)
    }

    /**
     * Saves the given document to the document repository.
     *
     * @param document The document to be saved.
     */
    override fun saveDocument(document: Document) {
        documentRepository.save(document)
    }

    /**
     * Finds a document by its unique id.
     *
     * @param id The id of the document to find.
     * @return The found document.
     * @throws NoSuchElementException If no document is found with the specified id.
     */
    override fun findDocumentById(id: String): Document {
        return documentRepository.findById(UUID.fromString(id))
            ?: throw NoSuchElementException("No document found with id: $id ")
    }

    /**
     * Finds a document by its ID.
     *
     * @param id The ID of the document to find.
     * @return The document with the specified ID.
     * @throws NoSuchElementException If no document is found with the specified ID.
     */
    override fun findDocumentById(id: UUID): Document {
        return documentRepository.findById(id) ?: throw NoSuchElementException("No document found with id: $id ")
    }

    /**
     * Retrieves all documents that the current user has access to.
     *
     * @return a list of documents that the current user has access to.
     */
    override fun getAllDocument(): List<Document> {
        val files = documentRepository.findAll().filter { d ->
            documentAccessService.userHasAccess(
                documentId = d.id, userId = userService.getCurrentUser().id
            )
        }.toList()
        return files
    }

    /**
     * Retrieves all other documents accessible by the current user.
     *
     * @return a list of documents accessible by the current user.
     */
    override fun getAllOtherDocument(): List<Document> {
        val currentUser = userService.getCurrentUser()
        val documentIds = currentUser.documentAccesses.stream().map { it.documentId }
        return documentIds.map { findDocumentById(it) }.toList()
    }

    /**
     * Returns the total number of uploading documents.
     *
     * @return The total number of uploading documents.
     */
    override fun getAllUploadingDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.UPLOADING).size
    }

    /**
     * Retrieves the total number of documents in the "EXTRACTING" status.
     *
     * @return The count of documents in "EXTRACTING" status.
     */
    override fun getAllExtractingDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.EXTRACTING).size
    }

    /**
     * Returns the count of all new documents.
     *
     * @return The count of all new documents.
     */
    override fun getAllNewDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.NEW).size
    }

    /**
     * Delete a document by its ID.
     *
     * @param documentId the ID of the document to be deleted
     */
    override fun deleteDocument(documentId: UUID) {
        val document = documentRepository.findById(documentId)!!
        document.deleted = true
        log.info { "Deleted document with id: $documentId" }
        documentRepository.save(document)
    }

    /**
     * Renames a document's filename.
     *
     * @param documentId The ID of the document to be renamed.
     * @param newFilename The new filename to rename the document to.
     * @return The document with the updated filename.
     */
    override fun renameDocument(documentId: UUID, newFilename: String): Document {
        val document = documentRepository.findById(documentId)!!
        document.filename = newFilename
        log.info { "Renamed document filename, new filename: $newFilename" }
        documentRepository.save(document)
        return document
    }

    /**
     * Updates the annotations of a document with the specified document ID.
     *
     * @param documentId The ID of the document to update.
     * @param annotations The new list of annotations to be assigned to the document.
     */
    override fun updateAnnotation(documentId: UUID, annotations: List<AttributeKeyValueModel>) {
        val document = documentRepository.findById(documentId)!!
        document.annotatedData = annotations
        log.info { "Updated document annotation data" }

        (document.annotatedData.stream().forEach { println("${it.key}  ${it.value}") })

        documentRepository.save(document)
    }

    /**
     * Locks the document with the given ID.
     *
     * @param documentId the ID of the document to be locked
     * @return the locked document
     */
    override fun lockDocument(documentId: UUID): Document {
        val document = documentRepository.findById(documentId)!!
        document.isLocked = true
        document.lockByUser = userService.getCurrentUser().id
        log.info { "Locked document with id: $documentId" }
        documentRepository.save(document)
        return document
    }

    /**
     * Unlocks a document with the given ID.
     *
     * @param documentId The ID of the document to unlock.
     * @return The unlocked document.
     */
    override fun unlockDocument(documentId: UUID): Document {
        val document = documentRepository.findById(documentId)!!
        document.isLocked = false
        log.info { "Unlocked document with id: $documentId" }
        documentRepository.save(document)
        return document
    }

    /**
     * Adds document access for a user.
     *
     * @param documentId The ID of the document to add access to.
     * @param email The email of the user to grant access to the document.
     * @return The updated document after adding access.
     */
    override fun addDocumentAccess(documentId: UUID, email: String): Document {
        val document = findDocumentById(documentId)
        val user = userService.findByEmail(email)
        val documentAccess = documentAccessService.createAccess(documentId = documentId, userId = user.id)
        document.documentAccesses.add(documentAccess)
        documentRepository.save(document)
        user.documentAccesses.add(documentAccess)
        userRepository.save(user)
        log.info { "Add document access for user: ${user.id}, document: $documentId" }
        return document
    }

    /**
     * Removes document access for a specific user from the document with the given ID.
     *
     * @param documentId The ID of the document from which to remove access.
     * @param userId The ID of the user for whom to remove access.
     */
    override fun removeDocumentAccess(documentId: UUID, userId: UUID) {
        val documentAccess = documentAccessService.removeAccess(documentId = documentId, userId = userId)
        val document = findDocumentById(documentId)
        document.documentAccesses.remove(documentAccess)
        documentRepository.save(document)
    }

    /**
     * Retrieves all the documents within a specified folder.
     *
     * @param folderId The ID of the folder whose documents need to be retrieved.
     * @return A list of documents within the specified folder.
     */
    override fun getAllDocumentInFolder(folderId: UUID): List<Document> {
        val folder = folderService.findById(folderId)
        return folder.documentIds.map { findDocumentById(it) }.filter { it.deleted == false }
    }

    /**
     * Searches for documents based on the given text.
     *
     * @param text The text to search for.
     * @return A list of documents matching the search criteria.
     */
    override fun search(text: String) = with(userService.getCurrentUser()) {
        val folderDocumentIds = folderAccesses
            .flatMap { folderService.findById(it.folderId).documentIds }

        documentRepository.searchByAllTextDescription(text).filter {
            documentAccessService.userHasAccess(it.id, id) || folderDocumentIds.contains(it.id)
        }
    }
}