package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import java.util.*

interface FolderAccessService {
    fun findById(id: UUID): FolderAccess?
    fun createAccess(folderId: UUID, userId: UUID): FolderAccess
    fun removeAccess(folderId: UUID, userId: UUID): FolderAccess
    fun userAccessToFolder(folderId: UUID, userId: UUID): FolderAccess?
    fun userHasAccess(folderId: UUID, userId: UUID): Boolean
    fun deleteAccess(folderAccess: FolderAccess)
}