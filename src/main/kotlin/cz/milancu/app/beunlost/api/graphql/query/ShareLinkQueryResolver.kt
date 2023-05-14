package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.ShareLink
import cz.milancu.app.beunlost.service.ShareLinkService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class ShareLinkQueryResolver(
    private val shareLinkService: ShareLinkService
) : GraphQLQueryResolver {
    fun getShareLink(documentId: UUID): ShareLink {
        return shareLinkService.createShareLink(documentId)
    }
}