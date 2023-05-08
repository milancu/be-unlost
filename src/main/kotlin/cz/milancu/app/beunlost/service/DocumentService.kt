package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.*
import org.apache.catalina.core.ApplicationPart
import java.util.*

interface DocumentService {
    fun uploadDocument(file: ApplicationPart, folderId: UUID?)
    fun saveDocument(document: Document)
    fun findDocumentById(id: String): Document
    fun findDocumentById(id: UUID): Document
    fun getAllDocument(): List<Document>
    fun getAllOtherDocument():List<Document>
    fun getAllUploadingDocument(): Int
    fun getAllExtractingDocument(): Int
    fun getAllNewDocument(): Int
    fun deleteDocument(documentId: UUID)
    fun renameDocument(documentId: UUID, newFilename: String):Document
    fun updateAnnotation(documentId: UUID, annotations: List<AttributeKeyValueModel>)
    fun lockDocument(documentId: UUID):Document
    fun unlockDocument(documentId: UUID):Document
    fun addDocumentAccess(documentId: UUID, userId: UUID):Document
    fun removeDocumentAccess(documentId: UUID, userId: UUID)
    fun getAllDocumentInFolder(folderId: UUID): List<Document>
    fun search(text:String):List<Document>
}