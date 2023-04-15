package cz.milancu.app.beunlost.domain.model.entity

import cz.milancu.app.beunlost.domain.model.enum.DocumentStatus
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import org.hibernate.annotations.Where
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant
import java.util.*

@Document(indexName = "document")
@Where(clause = "deleted=false")
class Document(
    @Id
    var id: UUID = UUID.randomUUID(),

    @Field(type = FieldType.Text)
    var filename: String,

    @Field(type = FieldType.Text)
    var storageFilename: String? = null,

    @Field(type = FieldType.Date)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var createAt: Instant? = Instant.now(),

    var createByUser: UUID,

    @Field(type = FieldType.Nested)
    var customAnnotations: List<CustomAnnotation> = ArrayList(),

    var annotatedData: List<CustomAnnotation> = ArrayList(),

    @Field(type = FieldType.Text)
    var allTextDescription: String? = null,

    @Field(type = FieldType.Text)
    var documentStatus: DocumentStatus? = null,

    @Field(type = FieldType.Text)
    var imgLink: String? = null,


    var lockByUser: UUID? = null,

    var deleted: Boolean? = false,

    var isLocked: Boolean? = false,

    @ManyToMany
    @JoinTable(
        name = "DOCUMENT_ACCESS",
        joinColumns = [JoinColumn(name = "DOCUMENT_ID")],
        inverseJoinColumns = [JoinColumn(name = "ACCESS_ID")]
    )
    var documentAccesses: MutableList<DocumentAccess> = ArrayList()
)