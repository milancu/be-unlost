package cz.milancu.app.beunlost.domain.repository

import cz.milancu.app.beunlost.domain.model.entity.CustomSchema
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SchemaRepository : ElasticsearchRepository<CustomSchema, String> {
    fun findById(id: UUID):CustomSchema?
}