package cz.milancu.app.beunlost.domain.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.util.*

@Document(indexName = "schemaindex")
class CustomSchema(
    @Id
    var id: UUID = UUID.randomUUID(),
    var labels: List<String>
)