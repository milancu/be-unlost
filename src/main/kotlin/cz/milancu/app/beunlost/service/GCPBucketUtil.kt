package cz.milancu.app.beunlost.service

import com.google.cloud.storage.*
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
import mu.KotlinLogging
import org.apache.catalina.core.ApplicationPart
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private val log = KotlinLogging.logger { }

@Component
class GCPBucketUtil(
    @Value("\${gcp.bucket.name}")
    private val bucketName: String,
    @Value("\${gcp.url}")
    private val bucketLink: String,
    private val documentRepository: DocumentRepository
) {

    private val storage: Storage = StorageOptions.getDefaultInstance().service

    fun uploadFileToBucketAndSave(file: ApplicationPart, documentId: UUID) {
        val filename = file.submittedFileName.plus(documentId)
        val blobId = BlobId.of(bucketName, filename)
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.contentType).build()
        storage.create(blobInfo, file.inputStream.readAllBytes())

        val document = documentRepository.findById(documentId)
        document?.storageFilename = filename
        document?.imgLink = "$bucketLink/$bucketName/$filename"

        documentRepository.save(document!!)
        log.info { "Uploaded document with id: ${document.id} to GCP, Document status: ${document.documentStatus}" }
    }
}