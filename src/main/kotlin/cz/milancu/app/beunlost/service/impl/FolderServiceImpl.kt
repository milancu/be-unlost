package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import cz.milancu.app.beunlost.domain.repository.FolderRepository
import cz.milancu.app.beunlost.service.DocumentService
import cz.milancu.app.beunlost.service.FolderAccessService
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.UserService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

private val log = KotlinLogging.logger { }

@Service
@Transactional
class FolderServiceImpl(
    private val folderRepository: FolderRepository,
    private val folderAccessService: FolderAccessService,
    private val userService: UserService
) : FolderService {
    override fun findById(id: UUID): Folder {
        return folderRepository.findById(id) ?: throw NoSuchElementException("folder with id: $id not found")
    }

    override fun saveFolder(folder: Folder) {
        folderRepository.save(folder)
        log.info { "Saved folder with id: ${folder.id}" }
    }

    override fun createFolder(name: String): Folder {
        val currentUser = userService.getCurrentUser()
        val folder = Folder(
            name = name, createBy = currentUser.id, folderType = FolderAccessType.OWNER
        )
        saveFolder(folder)
        addFolderAccess(folderId = folder.id, userId = currentUser.id)
        log.info { "Created new folder with id: ${folder.id}" }
        return folder
    }

    override fun deleteFolder(folderId: UUID) {
        val folder = findById(folderId)
        folder.deleted = true
        saveFolder(folder)
        log.info { "Deleted folder with id: ${folder.id}" }
    }

    override fun renameFolder(folderId: UUID, newName: String): Folder {
        val folder = findById(folderId)
        folder.name = newName
        saveFolder(folder)
        log.info { "Renamed folder with id: ${folder.id}" }
        return folder
    }

    override fun addFolderAccess(folderId: UUID, userId: UUID) {
        val folder = findById(folderId)
        val folderAccess = folderAccessService.createAccess(folderId = folderId, userId = userId)
        folder.folderAccesses.add(folderAccess)
        if (userService.getCurrentUser().id != userId) {
            folder.folderType = FolderAccessType.SHARED
        }
        saveFolder(folder)
    }

    override fun removeFolderAccess(folderId: UUID, userId: UUID) {
        val folder = findById(folderId)
        val folderAccess = folderAccessService.removeAccess(folderId = folderId, userId = userId)
        folder.folderAccesses.remove(folderAccess)
        saveFolder(folder)
    }

    override fun addDocument(folderId: UUID, document: Document) {
        val folder = findById(folderId)
        folder.documentIds.add(document.id)
        saveFolder(folder)
    }

    override fun removeDocument(folderId: UUID, document: Document) {
        val folder = findById(folderId)
        folder.documentIds.remove(document.id)
        saveFolder(folder)
    }

    override fun getAllFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        return folderRepository.findAll().filter { f -> folderAccessService.userHasAccess(f.id, currentUser.id) }
    }

    override fun getAllSharedFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        val folderAccess =
            currentUser.folderAccesses.map { x -> x.folderId }
        return folderAccess.map { findById(it) }.filter { it.folderType == FolderAccessType.SHARED }
    }

    override fun getAllOwnFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        val folderAccess =
            currentUser.folderAccesses.filter { folderAccess -> folderAccess.accessType == FolderAccessType.OWNER }
                .map { x -> x.folderId }
        return folderAccess.map { findById(it) }.filter { it.folderType == FolderAccessType.OWNER }
    }

    override fun searchFolderByName(name: String): List<Folder> {
        return getAllFolder().filter { it.name.contains(name) || it.name == name }
    }
}