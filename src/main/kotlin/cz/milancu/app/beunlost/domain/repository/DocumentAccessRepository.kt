package cz.milancu.app.beunlost.domain.repository

import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DocumentAccessRepository : JpaRepository<DocumentAccess, Long> {

    fun findById(id: UUID): DocumentAccess?

    fun findDocumentAccessByDocumentIdAndUserId(
        @Param("documentId") documentId: UUID,
        @Param("userId") userId: UUID
    ): DocumentAccess?
}