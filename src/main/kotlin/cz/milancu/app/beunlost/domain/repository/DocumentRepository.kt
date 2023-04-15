package cz.milancu.app.beunlost.domain.repository

import cz.milancu.app.beunlost.domain.model.entity.Document
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface DocumentRepository : ElasticsearchRepository<Document, String> {
    fun findById(id: UUID): Document?
}