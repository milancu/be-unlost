package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.config.security.GoogleOAuth2AuthenticationToken
import cz.milancu.app.beunlost.domain.repository.DocumentAccessRepository
import cz.milancu.app.beunlost.domain.repository.UserRepository
import cz.milancu.app.beunlost.service.DocumentAccessService
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
class DocumentAccessServiceImplTest(
    @Autowired val documentAccessRepository: DocumentAccessRepository,
    @Autowired val documentAccessService: DocumentAccessService,
    @Autowired val userRepository: UserRepository
) {

    val userId: UUID = Utils.userId

    @AfterEach
    fun cleanDB() {
        documentAccessRepository.deleteAll()
    }

    @BeforeEach
    fun initSecurityContext() {
        val user = Utils.createUser()
        userRepository.save(user)
        val oauth2User = GoogleOAuth2AuthenticationToken(
            email = user.email,
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
    fun createAccess() {
        val documentId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        documentAccessService.createAccess(documentId, userId)

        assertEquals(1, documentAccessRepository.count())
    }

    @Test
    fun removeAccess() {
        val documentId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        documentAccessService.createAccess(documentId, userId)
        documentAccessService.removeAccess(documentId, userId)

        assertEquals(0, documentAccessRepository.count())
    }

    @Test
    fun userAccessToDocument() {
        val documentId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        val expected = documentAccessService.createAccess(documentId, userId)
        val result = documentAccessService.userAccessToDocument(documentId, userId)
        assertEquals(expected, result)
    }

    @Test
    fun userHasAccess() {
        val documentId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        val result = documentAccessService.userHasAccess(documentId, userId)
        assertEquals(result, true)
    }
}