package cz.milancu.app.beunlost.domain.model.entity

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

class AttributeKeyValueModel(
    @Field(type = FieldType.Text)
    val key: String? = null,
    @Field(type = FieldType.Text)
    val value: String? = null
)