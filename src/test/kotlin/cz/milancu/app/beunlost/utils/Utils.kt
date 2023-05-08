package cz.milancu.app.beunlost.utils

import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.domain.model.entity.User
import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import java.util.*

class Utils(
) {
    companion object {
        val userId: UUID = UUID.randomUUID()
        val folderId: UUID = UUID.randomUUID()
        val documentId: UUID = UUID.randomUUID()

        fun createUser(): User {
            return User(
                email = "email",
                firstname = "user",
                lastname = "test",
                imageUrl = "img",
                id = userId
            )
        }

        fun createDocument(): Document {
            return  Document(
                filename = "filename",
                createByUser = userId,
                documentStatus = DocumentStatus.READY,
                folderId = folderId
            )
        }

        fun createFolder(): Folder {
            return Folder(
                id = folderId,
                createBy = userId,
                folderType = FolderAccessType.OWNER,
                name = "Folder"
            )
        }
    }
}