package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.CustomOAuth2User
import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import cz.milancu.app.beunlost.domain.model.entity.User
import cz.milancu.app.beunlost.domain.repository.UserRepository
import cz.milancu.app.beunlost.service.UserService
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun findById(id: UUID): User {
        return userRepository.findById(id) ?: throw NoSuchElementException("User with id: $id not found")
    }

    override fun findByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw UsernameNotFoundException("No user found with email: $email")
    }

    override fun createUser(oauthUser: CustomOAuth2User) {
        if (userExits(email = oauthUser.getEmail())) return
        val user = User(
            email = oauthUser.getEmail(),
            firstname = oauthUser.getFirstname(),
            lastname = oauthUser.getLastname(),
            imageUrl = oauthUser.getImageUrl(),
        )
        log.info { "Created new user with id: ${user.id} and email: ${user.email}" }
        userRepository.save(user)
    }

    override fun getCurrentUser(): User {
        val email = SecurityContextHolder.getContext().authentication.name
        return findByEmail(email);
    }

    /**
     * Adds a folder access to a user.
     *
     * @param userId the UUID of the user
     * @param folderAccess the folder access to be added
     */
    override fun addFolderAccess(userId: UUID, folderAccess: FolderAccess) {
        val user = findById(userId)
        user.folderAccesses.add(folderAccess)
        log.info { "Assigned a folder access to a user: ${user.id}" }
        userRepository.save(user)
    }

    /**
     * Removes a folder access from a user's folder access list.
     *
     * @param userId The ID of the user.
     * @param folderAccess The folder access to be removed.
     */
    override fun removeFolderAccess(userId: UUID, folderAccess: FolderAccess) {
        val user = findById(userId)
        user.folderAccesses.remove(folderAccess)
        log.info { "Removed a folder access to a user: ${user.id}" }
        userRepository.save(user)
    }

    /**
     * Adds document access to a user.
     *
     * @param userId the ID of the user
     * @param documentAccess the document access to be added
     */
    override fun addDocumentAccess(userId: UUID, documentAccess: DocumentAccess) {
        val user = findById(userId)
        user.documentAccesses.add(documentAccess)
        log.info { "Assigned a document access to a user: ${user.id}" }
        userRepository.save(user)
    }

    /**
     * Removes a document access for a user.
     *
     * @param userId the ID of the user
     * @param documentAccess the document access to be removed
     */
    override fun removeDocumentAccess(userId: UUID, documentAccess: DocumentAccess) {
        val user = findById(userId)
        user.documentAccesses.remove(documentAccess)
        log.info { "Removed a document access to a user: ${user.id}" }
        userRepository.save(user)
    }

    /**
     * Checks if a user with the given email exists in the user repository.
     *
     * @param email The email of the user to check.
     * @return true if a user with the given email exists, false otherwise.
     */
    private fun userExits(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }
}