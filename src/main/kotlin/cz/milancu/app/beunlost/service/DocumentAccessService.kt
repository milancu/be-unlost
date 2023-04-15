package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import java.util.*

interface DocumentAccessService {
    fun findById(documentId: UUID): DocumentAccess?
    fun createAccess(documentId: UUID, userId: UUID): DocumentAccess
    fun removeAccess(documentId: UUID, userId: UUID): DocumentAccess
    fun userAccessToDocument(documentId: UUID, userId: UUID): DocumentAccess?
    fun userHasAccess(documentId: UUID, userId: UUID): Boolean
}