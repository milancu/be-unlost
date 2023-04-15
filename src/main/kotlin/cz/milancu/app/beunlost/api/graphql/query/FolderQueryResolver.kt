package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.service.FolderService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class FolderQueryResolver(
    private val folderService: FolderService
) : GraphQLQueryResolver {

    fun getAllFolder(): List<Folder> {
        return folderService.getAllFolder()
    }

    fun getAllSharedFolder(): List<Folder> {
        return folderService.getAllSharedFolder()
    }

    fun getAllOwnedFolder(): List<Folder> {
        return folderService.getAllOwnFolder()
    }

    fun getFolder(id: UUID): Folder {
        return folderService.findById(id)
    }
}