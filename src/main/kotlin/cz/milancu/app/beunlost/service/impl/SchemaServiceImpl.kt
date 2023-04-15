package cz.milancu.app.beunlost.service.impl

import cz.milancu.app.beunlost.domain.model.entity.CustomSchema
import cz.milancu.app.beunlost.domain.repository.SchemaRepository
import cz.milancu.app.beunlost.service.DocumentService
import cz.milancu.app.beunlost.service.FolderService
import cz.milancu.app.beunlost.service.SchemaService
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException

@Service
class SchemaServiceImpl(
    private val schemaRepository: SchemaRepository,
    private val folderService: FolderService,
) : SchemaService {

    override fun findById(id: UUID): CustomSchema {
        return schemaRepository.findById(id) ?: throw NoSuchElementException("Schema not found")
    }

    override fun createSchema(labels: List<String>, folderId: UUID) {
        val customSchema = CustomSchema(
            labels = labels
        )
        schemaRepository.save(customSchema)
        val folder = folderService.findById(folderId)
        folder.customSchemaId = customSchema.id
        folderService.saveFolder(folder)
    }

    override fun updateSchema(labels: List<String>, folderId: UUID) {
        val folder = folderService.findById(folderId)
        val customSchema = findById(folder.customSchemaId!!)
        customSchema.labels = labels
        schemaRepository.save(customSchema)
        //TODO change schema for each element
    }
}