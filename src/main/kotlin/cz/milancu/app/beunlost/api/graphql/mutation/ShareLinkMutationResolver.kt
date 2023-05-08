package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.domain.model.entity.ShareLink
import cz.milancu.app.beunlost.service.ShareLinkService
import graphql.kickstart.tools.GraphQLMutationResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class ShareLinkMutationResolver(
    private val shareLinkService: ShareLinkService
) : GraphQLMutationResolver {
    fun createShareLink(documentId: UUID): ShareLink {
        return shareLinkService.createShareLink(documentId)
    }
}