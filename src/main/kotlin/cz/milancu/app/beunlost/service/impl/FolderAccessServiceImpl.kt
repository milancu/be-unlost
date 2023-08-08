package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import cz.milancu.app.beunlost.domain.repository.FolderAccessRepository
import cz.milancu.app.beunlost.service.FolderAccessService
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.UserService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger { }

@Service
@Transactional
class FolderAccessServiceImpl(
    private val folderAccessRepository: FolderAccessRepository,
    private val userService: UserService,
) : FolderAccessService {
    /**
     * Finds a folder access by its ID.
     *
     * @param id The ID of the folder access.
     * @return The folder access with the specified ID, or null if not found.
     * @throws NoSuchElementException if no folder access with the specified ID is found.
     */
    override fun findById(id: UUID): FolderAccess? {
        return folderAccessRepository.findById(id)
            ?: throw NoSuchElementException("No folder access with id: $id found.")
    }

    /**
     * Creates a new folder access for the given folderId and userId.
     * If the userId matches the current user, the access type will be set to OWNER.
     * Otherwise, the access type will be set to SHARED.
     *
     * @param folderId The unique identifier of the folder.
     * @param userId The unique identifier of the user.
     *
     * @return The newly created FolderAccess object.
     */
    override fun createAccess(folderId: UUID, userId: UUID): FolderAccess {
        val folderAccess = FolderAccess(
            folderId = folderId,
            userId = userId,
            accessType = if (checkIsCurrentUser(userId)) FolderAccessType.OWNER else FolderAccessType.SHARED
        )
        folderAccessRepository.save(folderAccess)
        userService.addFolderAccess(folderAccess = folderAccess, userId = userId)
        log.info { "Created folder access. folderId: $folderId" }
        return folderAccess
    }

    /**
     * Checks if the provided user ID matches the ID of the current user.
     *
     * @param userId The ID of the user to check.
     * @return true if the provided user ID matches the ID of the current user, false otherwise.
     */
    private fun checkIsCurrentUser(userId: UUID): Boolean {
        return userService.getCurrentUser().id == userId
    }

    /**
     * Removes access from a folder for a user.
     *
     * @param folderId The ID of the folder.
     * @param userId The ID of the user.
     * @return The deleted FolderAccess object.
     */
    override fun removeAccess(folderId: UUID, userId: UUID): FolderAccess {
        val folderAccess = userAccessToFolder(folderId, userId)
        userService.removeFolderAccess(folderAccess = folderAccess, userId = userId)
        folderAccessRepository.delete(folderAccess)
        log.info { "Folder access deleted, folder: $folderId, user: $userId" }
        return folderAccess
    }

    /**
     * Retrieve the access level of a user to a folder.
     *
     * @param folderId The ID of the folder to check access for.
     * @param userId The ID of the user to check access for.
     * @return The access level of the user to the folder.
     * @throws NoSuchElementException if no folder access is found for the given folder and user.
     */
    override fun userAccessToFolder(folderId: UUID, userId: UUID): FolderAccess {
        return folderAccessRepository.findFolderAccessByFolderIdAndUserId(folderId, userId)
            ?: throw NoSuchElementException("No folder access found!")
    }

    /**
     * Checks if a given user has access to a specific folder.
     *
     * @param folderId The ID of the folder to check access for.
     * @param userId The ID of the user to check access for.
     * @return True if the user has access to the folder, false otherwise.
     */
    override fun userHasAccess(folderId: UUID, userId: UUID): Boolean {
        return folderAccessRepository.findFolderAccessByFolderIdAndUserId(folderId, userId) != null
    }

    /**
     * Deletes the given folder access for the current user.
     *
     * @param folderAccess the folder access to delete
     */
    override fun deleteAccess(folderAccess: FolderAccess) {
        userService.removeFolderAccess(userService.getCurrentUser().id, folderAccess)
        folderAccess.deleted = true
        folderAccessRepository.save(folderAccess)
        log.info { "Deleted folder access" }
    }
}