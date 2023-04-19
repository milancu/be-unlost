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
    override fun findById(id: UUID): FolderAccess? {
        return folderAccessRepository.findById(id)
            ?: throw NoSuchElementException("No folder access with id: $id found.")
    }

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

    private fun checkIsCurrentUser(userId: UUID): Boolean {
        return userService.getCurrentUser().id == userId
    }

    override fun removeAccess(folderId: UUID, userId: UUID): FolderAccess {
        val folderAccess = userAccessToFolder(folderId, userId)
        userService.removeFolderAccess(folderAccess = folderAccess, userId = userId)
        folderAccessRepository.delete(folderAccess)
        log.info { "Folder access deleted, folder: $folderId, user: $userId" }
        return folderAccess
    }

    override fun userAccessToFolder(folderId: UUID, userId: UUID): FolderAccess {
        return folderAccessRepository.findFolderAccessByFolderIdAndUserId(folderId, userId)
            ?: throw NoSuchElementException("No folder access found!")
    }

    override fun userHasAccess(folderId: UUID, userId: UUID): Boolean {
        return folderAccessRepository.findFolderAccessByFolderIdAndUserId(folderId, userId) != null
    }
}