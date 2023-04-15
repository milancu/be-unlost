package cz.milancu.app.beunlost.domain.repository

import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FolderAccessRepository : JpaRepository<FolderAccess, Long> {
    fun findById(id: UUID): FolderAccess?
    fun findFolderAccessByFolderIdAndUserId(folderId: UUID, userId: UUID): FolderAccess?
}