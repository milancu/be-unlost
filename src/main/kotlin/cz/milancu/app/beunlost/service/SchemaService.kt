package cz.milancu.app.beunlost.service

import cz.milancu.app.beunlost.domain.model.entity.CustomSchema
import java.util.*
import kotlin.collections.List

interface SchemaService {
    fun findById(id:UUID):CustomSchema
    fun createSchema(labels: List<String>, folderId:UUID)
    fun updateSchema(labels: List<String>, folderId:UUID)
}