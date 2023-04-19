package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.service.DocumentService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class DocumentQueryResolver(
    private val documentService: DocumentService
) : GraphQLQueryResolver {

    fun getDocument(id: String): Document {
        return documentService.findDocumentById(id)!!
    }

    fun getAllDocument(): List<Document> {
        return documentService.getAllDocument();
    }

    fun getAllDocumentInFolder(folderId: UUID): List<Document> {
        return documentService.getAllDocumentInFolder(folderId)
    }

    fun getSumOfUploadingFile(): Int {
        return documentService.getAllUploadingDocument()
    }

    fun getSumOfExtractingFile(): Int {
        return documentService.getAllExtractingDocument()
    }

    fun getSumOfNewFile(): Int {
        return documentService.getAllNewDocument()
    }

    fun searchFileByText(text: String): List<Document> {
        return documentService.search(text)
    }
}