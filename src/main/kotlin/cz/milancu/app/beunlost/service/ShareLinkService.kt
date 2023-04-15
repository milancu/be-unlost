package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.ShareLink
import java.util.*

interface ShareLinkService {
    fun createShareLink(documentId: UUID): ShareLink
}