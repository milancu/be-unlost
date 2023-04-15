package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.ShareLink
import cz.milancu.app.beunlost.domain.repository.ShareLinkRepository
import cz.milancu.app.beunlost.service.DocumentService
import cz.milancu.app.beunlost.service.ShareLinkService
import cz.milancu.app.beunlost.service.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShareLinkServiceImpl(
    private val shareLinkRepository: ShareLinkRepository,
    private val documentService: DocumentService,
    private val userService: UserService
) : ShareLinkService {
    override fun createShareLink(documentId: UUID): ShareLink {
        val document = documentService.findDocumentById(documentId)

        val shareLink = ShareLink(
            link = document.imgLink!!,
            documentId = documentId,
            createBy = userService.getCurrentUser().id
        )
        shareLinkRepository.save(shareLink)
        return shareLink
    }
}