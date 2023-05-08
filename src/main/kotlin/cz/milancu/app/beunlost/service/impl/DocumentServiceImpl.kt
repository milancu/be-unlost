package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.*
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
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
    private val elasticsearchOperations: ElasticsearchOperations
) : DocumentService {
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

        addDocumentAccess(documentId = document.id, userId = currentUser.id)
    }

    override fun saveDocument(document: Document) {
        documentRepository.save(document)
    }

    override fun findDocumentById(id: String): Document {
        return documentRepository.findById(UUID.fromString(id))
            ?: throw NoSuchElementException("No document found with id: $id ")
    }

    override fun findDocumentById(id: UUID): Document {
        return documentRepository.findById(id) ?: throw NoSuchElementException("No document found with id: $id ")
    }

    override fun getAllDocument(): List<Document> {
        val files = documentRepository.findAll().filter { d ->
            documentAccessService.userHasAccess(
                documentId = d.id, userId = userService.getCurrentUser().id
            )
        }.toList()
        return files
    }

    override fun getAllOtherDocument(): List<Document> {
        val currentUser = userService.getCurrentUser()
        val documentIds = currentUser.documentAccesses.stream().map { it.documentId }
        return documentIds.map { findDocumentById(it) }.toList()
    }

    override fun getAllUploadingDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.UPLOADING).size
    }

    override fun getAllExtractingDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.EXTRACTING).size
    }

    override fun getAllNewDocument(): Int {
        return documentRepository.findDocumentsByDocumentStatus(DocumentStatus.NEW).size
    }

    override fun deleteDocument(documentId: UUID) {
        val document = documentRepository.findById(documentId)!!
        document.deleted = true
        log.info { "Deleted document with id: $documentId" }
        documentRepository.save(document)
    }

    override fun renameDocument(documentId: UUID, newFilename: String): Document {
        val document = documentRepository.findById(documentId)!!
        document.filename = newFilename
        log.info { "Renamed document filename, new filename: $newFilename" }
        documentRepository.save(document)
        return document
    }

    override fun updateAnnotation(documentId: UUID, annotations: List<AttributeKeyValueModel>) {
        val document = documentRepository.findById(documentId)!!
        document.annotatedData = annotations
        log.info { "Updated document annotation data" }

        (document.annotatedData.stream().forEach { println("${it.key}  ${it.value}") })

        documentRepository.save(document)
    }

    override fun lockDocument(documentId: UUID): Document {
        val document = documentRepository.findById(documentId)!!
        document.isLocked = true
        document.lockByUser = userService.getCurrentUser().id
        log.info { "Locked document with id: $documentId" }
        documentRepository.save(document)
        return document
    }

    override fun unlockDocument(documentId: UUID): Document {
        val document = documentRepository.findById(documentId)!!
        document.isLocked = false
        log.info { "Unlocked document with id: $documentId" }
        documentRepository.save(document)
        return document
    }

    override fun addDocumentAccess(documentId: UUID, userId: UUID): Document {
        val document = findDocumentById(documentId)
        val documentAccess = documentAccessService.createAccess(documentId = documentId, userId = userId)
        document.documentAccesses.add(documentAccess)
        documentRepository.save(document)
        return document
    }

    override fun removeDocumentAccess(documentId: UUID, userId: UUID) {
        val documentAccess = documentAccessService.removeAccess(documentId = documentId, userId = userId)
        val document = findDocumentById(documentId)
        document.documentAccesses.remove(documentAccess)
        documentRepository.save(document)
    }

    override fun getAllDocumentInFolder(folderId: UUID): List<Document> {
        val folder = folderService.findById(folderId)
        return folder.documentIds.map { findDocumentById(it) }
    }

    override fun search(text: String): List<Document> {
        val folderDocumentIds = userService.getCurrentUser().folderAccesses
            .stream()
            .flatMap { folderService.findById(it.folderId).documentIds.stream() }
            .toList()

        val userDocumentIds = userService.getCurrentUser().documentAccesses
            .stream()
            .map { it.documentId }
            .toList()

        val documents = folderDocumentIds + userDocumentIds


        val result = documents.stream().map { findDocumentById(it) }.filter {
            it.allTextDescription!!.uppercase().contains(text.uppercase())
        }.toList()


        return result
    }
}