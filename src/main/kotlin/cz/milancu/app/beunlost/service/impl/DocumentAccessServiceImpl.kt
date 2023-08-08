package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import cz.milancu.app.beunlost.domain.repository.DocumentAccessRepository
import cz.milancu.app.beunlost.service.DocumentAccessService
import cz.milancu.app.beunlost.service.DocumentService
import cz.milancu.app.beunlost.service.UserService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger { }

@Service
@Transactional
class DocumentAccessServiceImpl(
    private val documentAccessRepository: DocumentAccessRepository,
) : DocumentAccessService {
    /**
     * Finds a document access by its ID.
     *
     * @param documentId The ID of the document access to find.
     * @return The found document access, or null if not found.
     * @throws NoSuchElementException if no document access is found with the given ID.
     */
    override fun findById(documentId: UUID): DocumentAccess? {
        return documentAccessRepository.findById(documentId)
            ?: throw NoSuchElementException("Document access with id:$documentId not found")
    }

    /**
     * Creates a document access for the given document ID and user ID.
     *
     * @param documentId The ID of the document for which access needs to be created.
     * @param userId The ID of the user for whom the access is being created.
     * @return The created DocumentAccess object.
     */
    override fun createAccess(documentId: UUID, userId: UUID): DocumentAccess {
        val documentAccess = DocumentAccess(
            documentId = documentId,
            userId = userId
        )
        log.info { "Document access created, document: $documentId, user: $userId" }
        documentAccessRepository.save(documentAccess)
        return documentAccess
    }

    /**
     * Remove the access of a user to a document.
     *
     * @param documentId The ID of the document.
     * @param userId The ID of the user.
     * @return The removed DocumentAccess object.
     */
    override fun removeAccess(documentId: UUID, userId: UUID): DocumentAccess {
        val documentAccess = userAccessToDocument(documentId, userId)
        documentAccessRepository.delete(documentAccess)
        log.info { "Document access deleted, document: $documentId, user: $userId" }
        return documentAccess
    }

    /**
     * Retrieves the document access for a specific document and user.
     *
     * @param documentId The ID of the document.
     * @param userId The ID of the user.
     * @return The document access for the specified document and user.
     * @throws NoSuchElementException if no document access is found.
     */
    override fun userAccessToDocument(documentId: UUID, userId: UUID): DocumentAccess {
        return documentAccessRepository.findDocumentAccessByDocumentIdAndUserId(documentId, userId)
            ?: throw NoSuchElementException("No document access found!")
    }

    /**
     * Checks if the given user has access to a document.
     *
     * @param documentId The ID of the document to check access for.
     * @param userId The ID of the user to check access for.
     * @return `true` if the user has access to the document, `false` otherwise.
     */
    override fun userHasAccess(documentId: UUID, userId: UUID): Boolean {
        return documentAccessRepository.findDocumentAccessByDocumentIdAndUserId(documentId, userId) != null
    }
}