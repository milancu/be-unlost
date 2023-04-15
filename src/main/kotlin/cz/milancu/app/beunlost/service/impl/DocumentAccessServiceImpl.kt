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
    override fun findById(documentId: UUID): DocumentAccess? {
        return documentAccessRepository.findById(documentId)
            ?: throw NoSuchElementException("Document access with id:$documentId not found")
    }

    override fun createAccess(documentId: UUID, userId: UUID): DocumentAccess {
        val documentAccess = DocumentAccess(
            documentId = documentId,
            userId = userId
        )
        log.info { "Document access created, document: $documentId, user: $userId" }
        documentAccessRepository.save(documentAccess)
        return documentAccess
    }

    override fun removeAccess(documentId: UUID, userId: UUID): DocumentAccess {
        val documentAccess = userAccessToDocument(documentId, userId)
        documentAccessRepository.delete(documentAccess)
        log.info { "Document access deleted, document: $documentId, user: $userId" }
        return documentAccess
    }

    override fun userAccessToDocument(documentId: UUID, userId: UUID): DocumentAccess {
        return documentAccessRepository.findDocumentAccessByDocumentIdAndUserId(documentId, userId)
            ?: throw NoSuchElementException("No document access found!")
    }

    override fun userHasAccess(documentId: UUID, userId: UUID): Boolean {
        return documentAccessRepository.findDocumentAccessByDocumentIdAndUserId(documentId, userId) != null
    }
}