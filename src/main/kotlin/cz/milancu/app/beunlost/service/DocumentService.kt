package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.CustomAnnotation
import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import org.apache.catalina.core.ApplicationPart
import java.util.*

interface DocumentService {
    fun uploadDocument(file: ApplicationPart, folderId:UUID?)
    fun saveDocument(document: Document)
    fun findDocumentById(id: String): Document
    fun findDocumentById(id: UUID): Document
    fun getAllDocument(): List<Document>
    fun deleteDocument(documentId: UUID)
    fun renameDocument(documentId: UUID, newFilename: String)
    fun updateAnnotation(documentId: UUID, annotations: List<CustomAnnotation>)
    fun lockDocument(documentId: UUID)
    fun unlockDocument(documentId: UUID)
    fun addDocumentAccess(documentId: UUID, userId: UUID)
    fun removeDocumentAccess(documentId: UUID, userId: UUID)
    fun getAllDocumentInFolder(folderId: UUID):List<Document>
}