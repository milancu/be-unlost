package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.config.security.GoogleOAuth2AuthenticationToken
import cz.milancu.app.beunlost.domain.model.entity.AttributeKeyValueModel
import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.repository.DocumentAccessRepository
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
import cz.milancu.app.beunlost.domain.repository.FolderRepository
import cz.milancu.app.beunlost.domain.repository.UserRepository
import cz.milancu.app.beunlost.service.DocumentService
import cz.milancu.app.beunlost.service.GCPBucketUtil
import cz.milancu.app.beunlost.service.GCPVisionUtil
import cz.milancu.app.beunlost.utils.Utils
import org.apache.catalina.core.ApplicationPart
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.TestPropertySource
import java.io.File
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class DocumentServiceImplTest(
    @Autowired val documentService: DocumentService,
    @Autowired val documentRepository: DocumentRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val folderRepository: FolderRepository,
    @Autowired val documentAccessRepository: DocumentAccessRepository,
    @Autowired val visionUtil: GCPVisionUtil,
    @Autowired val bucketUtil: GCPBucketUtil,
) {

    val userId: UUID = Utils.userId
    val folderId: UUID = Utils.folderId

    @AfterEach
    fun cleanDB() {
        documentAccessRepository.deleteAll()
        documentRepository.deleteAll()
        folderRepository.deleteAll()
    }

    @BeforeEach
    fun initTenantContext() {
        userRepository.save(Utils.createUser())
        folderRepository.save(Utils.createFolder())

        val oauth2User = GoogleOAuth2AuthenticationToken(
            email = "email",
            authorities = Collections.singleton(SimpleGrantedAuthority("user"))
        )
        val authentication = UsernamePasswordAuthenticationToken(
            oauth2User,
            null,
            oauth2User.authorities
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun uploadDocument() {
        val file = File("src/test/kotlin/cz/milancu/app/beunlost/service/impl/Screenshot_3.png")
        val multipartFile = ApplicationPart(null, file)

        documentService.uploadDocument(folderId = folderId, file = multipartFile)

        assertEquals(1, documentRepository.count())
    }

    @Test
    fun getAllUploadingDocument() {
        for (i in 0..10) {
            val document = Document(
                filename = "filename",
                createByUser = userId,
                documentStatus = if (i % 2 == 0) DocumentStatus.UPLOADING else DocumentStatus.EXTRACTING,
                folderId = folderId
            )
            documentRepository.save(document)
        }
        val result = documentService.getAllUploadingDocument()
        assertEquals(5, result)
    }

    @Test
    fun getAllExtractingDocument() {
        for (i in 0..10) {
            val document = Document(
                filename = "filename",
                createByUser = userId,
                documentStatus = if (i % 2 == 0) DocumentStatus.UPLOADING else DocumentStatus.EXTRACTING,
                folderId = folderId
            )
            documentRepository.save(document)
        }
        val result = documentService.getAllUploadingDocument()
        assertEquals(5, result)
    }

    @Test
    fun deleteDocument() {
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)
        documentService.deleteDocument(documentId)

        assertEquals(0, documentRepository.count())
    }

    @Test
    fun renameDocument() {
        val newName = "hodlcube"
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)

        documentService.renameDocument(documentId, newName)
        val result = documentService.findDocumentById(documentId)

        assertEquals(newName, result.filename)
    }

    @Test
    fun updateAnnotation() {
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)

        val annotatedData = ArrayList<AttributeKeyValueModel>()
        annotatedData.add(AttributeKeyValueModel(key = "key", value = "value"))
        annotatedData.add(AttributeKeyValueModel(key = "key1", value = "value"))
        annotatedData.add(AttributeKeyValueModel(key = "key3", value = "value"))
        documentService.updateAnnotation(documentId, annotatedData)

        val result = documentService.findDocumentById(documentId)


        assertEquals(annotatedData, result.annotatedData)
    }

    @Test
    fun lockDocument() {
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)
        documentService.lockDocument(documentId)

        val result = documentService.findDocumentById(documentId)

        assertEquals(false, result.isLocked)
    }

    @Test
    fun unlockDocument() {
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)
        documentService.unlockDocument(documentId)

        val result = documentService.findDocumentById(documentId)

        assertEquals(true, result.isLocked)
    }

    @Test
    fun addDocumentAccess() {
        val newUser = UUID.randomUUID()
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)
        documentService.addDocumentAccess(documentId, newUser)


        assertEquals(2, documentAccessRepository.count())
    }

    @Test
    fun removeDocumentAccess() {
        val newUser = UUID.randomUUID()
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)
        documentService.addDocumentAccess(documentId, newUser)
        documentService.removeDocumentAccess(documentId, newUser)


        assertEquals(1, documentAccessRepository.count())
    }

    @Test
    fun search() {
        val documentId = UUID.randomUUID()
        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folderId,
            id = documentId
        )
        documentRepository.save(document)

        val annotatedData = ArrayList<AttributeKeyValueModel>()
        annotatedData.add(AttributeKeyValueModel(key = "key", value = "value1"))
        annotatedData.add(AttributeKeyValueModel(key = "key1", value = "value2"))
        annotatedData.add(AttributeKeyValueModel(key = "key3", value = "value3"))
        documentService.updateAnnotation(documentId, annotatedData)

        val result = documentService.search("value3")
        assertEquals(1, result.size)
    }
}