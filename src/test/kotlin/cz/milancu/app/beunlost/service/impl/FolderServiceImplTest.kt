package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.config.security.GoogleOAuth2AuthenticationToken
import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import cz.milancu.app.beunlost.domain.repository.*
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.utils.Utils
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
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class FolderServiceImplTest(
    @Autowired val documentAccessRepository: DocumentAccessRepository,
    @Autowired val documentRepository: DocumentRepository,
    @Autowired val folderRepository: FolderRepository,
    @Autowired val folderService: FolderService,
    @Autowired val folderAccessRepository: FolderAccessRepository,
    @Autowired val userRepository: UserRepository,
) {

    val userId: UUID = Utils.userId
    val folderId: UUID = Utils.folderId

    @AfterEach
    fun cleanDB() {
        documentAccessRepository.deleteAll()
        documentRepository.deleteAll()
        folderRepository.deleteAll()
        folderAccessRepository.deleteAll()
    }

    @BeforeEach
    fun initTenantContext() {
        folderRepository.save(Utils.createFolder())
        userRepository.save(Utils.createUser())
        documentRepository.save(Utils.createDocument())

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
    fun createFolder() {
        val folderName = "New folder"
        folderService.createFolder(folderName)

        assertEquals(1, folderRepository.count())
    }

    @Test
    fun deleteFolder() {
        val folderName = "New folder"
        val folder = folderService.createFolder(folderName)
        folderService.deleteFolder(folder.id)

        assertEquals(0, folderRepository.count())
    }

    @Test
    fun renameFolder() {
        val folderName = "Folder"
        val folderNewName = "New Folder"
        val folder = folderService.createFolder(folderName)
        val newFolder = folderService.renameFolder(folder.id, folderNewName)

        assertEquals(folderNewName, newFolder.name)
    }

    @Test
    fun addFolderAccess() {
        val folderName = "Folder"
        val folder = folderService.createFolder(folderName)
        folderService.addFolderAccess(folder.id, userId)

        assertEquals(1, folderAccessRepository.count())
    }

    @Test
    fun removeFolderAccess() {
        val folderName = "Folder"
        val folder = folderService.createFolder(folderName)
        folderService.addFolderAccess(folder.id, userId)
        folderService.removeFolderAccess(folder.id, userId)

        assertEquals(0, folderAccessRepository.count())
    }

    @Test
    fun addDocument() {
        val folderName = "Folder"
        val folder = folderService.createFolder(folderName)

        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folder.id
        )

        folderService.addDocument(folder.id, document)
        assertEquals(1, folderRepository.findById(folder.id)?.documentIds!!.size)
    }

    @Test
    fun removeDocument() {
        val folderName = "Folder"
        val folder = folderService.createFolder(folderName)

        val document = Document(
            filename = "filename",
            createByUser = userId,
            documentStatus = DocumentStatus.EXTRACTING,
            folderId = folder.id
        )

        folderService.addDocument(folder.id, document)
        folderService.removeDocument(folder.id, document)
        assertEquals(0, folderRepository.findById(folder.id)?.documentIds!!.size)
    }

    @Test
    fun getAllFolder() {
        for (i in 0..10) {
            val folderName = "Folder$i"
            folderService.createFolder(folderName)
        }
        assertEquals(10, folderService.getAllFolder().size)
    }

    @Test
    fun getAllSharedFolder() {
        for (i in 0..10) {
            val folderName = "Folder$i"
            val folder = folderService.createFolder(name = folderName)
            folderService.addFolderAccess(folder.id, userId)
        }
        assertEquals(10, folderService.getAllFolder().filter { it.folderType == FolderAccessType.SHARED }.size)
    }

    @Test
    fun getAllOwnFolder() {
        for (i in 0..10) {
            val folderName = "Folder$i"
            folderService.createFolder(name = folderName)
        }
        assertEquals(10, folderService.getAllFolder().filter { it.folderType == FolderAccessType.OWNER }.size)
    }

    @Test
    fun searchFolderByName() {
        val folderName = "New folder"
        folderService.createFolder(folderName)
        val result = folderService.searchFolderByName(folderName)

        assertEquals(1, result.size)
    }
}