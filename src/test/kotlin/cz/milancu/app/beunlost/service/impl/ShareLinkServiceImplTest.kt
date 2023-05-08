package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.config.security.GoogleOAuth2AuthenticationToken
import cz.milancu.app.beunlost.domain.repository.DocumentRepository
import cz.milancu.app.beunlost.domain.repository.FolderRepository
import cz.milancu.app.beunlost.domain.repository.ShareLinkRepository
import cz.milancu.app.beunlost.domain.repository.UserRepository
import cz.milancu.app.beunlost.service.ShareLinkService
import cz.milancu.app.beunlost.utils.Utils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.test.context.TestPropertySource
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ShareLinkServiceImplTest(
    @Autowired val shareLinkService: ShareLinkService,
    @Autowired val shareLinkRepository: ShareLinkRepository,
    @Autowired val documentRepository: DocumentRepository,
    @Autowired val folderRepository: FolderRepository,
    @Autowired val userRepository: UserRepository

) {

    private val documentId = Utils.documentId

    @Autowired

    @AfterEach
    fun cleanDB() {
        shareLinkRepository.deleteAll()
        documentRepository.deleteAll()
        folderRepository.deleteAll()
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
    fun createShareLink() {
        shareLinkService.createShareLink(documentId)
        assertEquals(1, shareLinkRepository.count())
    }
}