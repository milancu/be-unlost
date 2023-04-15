package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.CustomOAuth2User
import cz.milancu.app.beunlost.domain.model.entity.DocumentAccess
import cz.milancu.app.beunlost.domain.model.entity.FolderAccess
import cz.milancu.app.beunlost.domain.model.entity.User
import java.util.*

interface UserService {
    fun findById(id: UUID): User
    fun findByEmail(email: String): User
    fun createUser(oauthUser: CustomOAuth2User);
    fun getCurrentUser(): User
    fun addFolderAccess(folderAccess: FolderAccess)
    fun removeFolderAccess(folderAccess: FolderAccess)
    fun addDocumentAccess(documentAccess: DocumentAccess)
    fun removeDocumentAccess(documentAccess: DocumentAccess)

}