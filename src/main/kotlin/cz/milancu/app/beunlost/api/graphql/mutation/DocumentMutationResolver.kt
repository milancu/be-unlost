package cz.milancu.app.beunlost.api.graphql.mutation

import cz.milancu.app.beunlost.service.DocumentService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.schema.DataFetchingEnvironment
import org.apache.catalina.core.ApplicationPart
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*


@Component
class DocumentMutationResolver(
    private val documentService: DocumentService,
) : GraphQLMutationResolver {
    @Throws(IOException::class)
    fun uploadImage(parts: List<ApplicationPart>, folderId: UUID?, env: DataFetchingEnvironment): Boolean {
        val files: List<ApplicationPart> = env.getArgument("files")
        files.stream().forEach { documentService.uploadDocument(file = it, folderId = folderId) }
        return true
    }
}

