package cz.milancu.app.beunlost.api.graphql.query

import cz.milancu.app.beunlost.domain.model.entity.CustomSchema
import cz.milancu.app.beunlost.domain.model.entity.Document
import cz.milancu.app.beunlost.domain.model.entity.Folder
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.SchemaService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class FolderQueryResolver(
    private val folderService: FolderService,
    private val schemaService: SchemaService
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

    fun getFolderSchema(id: UUID): CustomSchema {
        return schemaService.findById(folderService.findById(id).customSchemaId!!)
    }

    fun searchFolderByName(name: String): List<Folder> {
        return folderService.searchFolderByName(name)
    }
}