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
        val accesses = folder.folderAccesses
//        folder.folderAccesses.clear()
        println("accesses size: ${accesses.size}")
        accesses.forEach { folderAccessService.deleteAccess(it) }
        folder.folderAccesses.clear()
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

    /**
     * Adds folder access for a user to a specific folder.
     *
     * @param folderId The ID of the folder.
     * @param userId The ID of the user.
     */
    override fun addFolderAccess(folderId: UUID, userId: UUID) {
        val folder = findById(folderId)
        val folderAccess = folderAccessService.createAccess(folderId = folderId, userId = userId)
        folder.folderAccesses.add(folderAccess)
        if (userService.getCurrentUser().id != userId) {
            folder.folderType = FolderAccessType.SHARED
        }
        saveFolder(folder)
    }

    /**
     * Removes folder access for a specific user.
     *
     * @param folderId The ID of the folder to remove access from.
     * @param userId The ID of the user to remove access for.
     */
    override fun removeFolderAccess(folderId: UUID, userId: UUID) {
        val folder = findById(folderId)
        val folderAccess = folderAccessService.removeAccess(folderId = folderId, userId = userId)
        folder.folderAccesses.remove(folderAccess)
        saveFolder(folder)
    }

    /**
     * Adds a document to the specified folder.
     *
     * @param folderId The ID of the folder.
     * @param document The document to be added.
     */
    override fun addDocument(folderId: UUID, document: Document) {
        val folder = findById(folderId)
        folder.documentIds.add(document.id)
        saveFolder(folder)
    }

    /**
     * Removes a document from the specified folder.
     *
     * @param folderId The ID of the folder from which the document should be removed.
     * @param document The document to be removed.
     */
    override fun removeDocument(folderId: UUID, document: Document) {
        val folder = findById(folderId)
        folder.documentIds.remove(document.id)
        saveFolder(folder)
    }

    /**
     * Retrieves all folders that the current user has access to.
     *
     * @return a list of folders that the current user has access to.
     */
    override fun getAllFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        return folderRepository.findAll().filter { f -> folderAccessService.userHasAccess(f.id, currentUser.id) }
    }

    /**
     * Retrieves a list of all shared folders accessible to the current user.
     *
     * @return A list of [Folder] objects representing the shared folders.
     */
    override fun getAllSharedFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        val folderAccess =
            currentUser.folderAccesses.filter { !it.deleted }.map { x -> x.folderId }
        return folderAccess.map { findById(it) }.filter { it.folderType == FolderAccessType.SHARED }
    }

    /**
     * Retrieves a list of all folders owned by the current user.
     *
     * @return A list of Folder objects representing the owned folders.
     */
    override fun getAllOwnFolder(): List<Folder> {
        val currentUser = userService.getCurrentUser()
        val folderAccess =
            currentUser.folderAccesses.filter { !it.deleted }
                .filter { folderAccess -> folderAccess.accessType == FolderAccessType.OWNER }
                .map { x -> x.folderId }
        return folderAccess.map { findById(it) }.filter { it.folderType == FolderAccessType.OWNER }
    }

    /**
     * Searches for folders with the given name.
     *
     * @param name The name to search for.
     * @return A list of folders matching the given name.
     */
    override fun searchFolderByName(name: String): List<Folder> {
        return getAllFolder().filter { it.name.contains(name) || it.name == name }
    }
}