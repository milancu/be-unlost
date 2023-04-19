package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import java.util.*


interface FolderService {
    fun findById(id: UUID): Folder
    fun saveFolder(folder: Folder)
    fun createFolder(name: String): Folder
    fun deleteFolder(folderId: UUID)
    fun renameFolder(folderId: UUID, newName: String)
    fun addFolderAccess(folderId: UUID, userId: UUID)
    fun removeFolderAccess(folderId: UUID, userId: UUID)
    fun addDocument(folderId: UUID, document: Document)
    fun removeDocument(folderId: UUID, document: Document)
    fun getAllFolder(): List<Folder>
    fun getAllSharedFolder(): List<Folder>
    fun getAllOwnFolder(): List<Folder>
    fun searchFolderByName(name: String): List<Folder>
}