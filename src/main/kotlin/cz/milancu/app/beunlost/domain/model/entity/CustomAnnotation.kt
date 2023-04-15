package cz.milancu.app.beunlost.domain.model.entity

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

class CustomAnnotation(
    @Field(type = FieldType.Text)
    val description: String,
    @Field(type = FieldType.Double)
    val x: Int,
    @Field(type = FieldType.Double)
    val y: Int,
    @Field(type = FieldType.Double)
    val width: Int,
    @Field(type = FieldType.Double)
    val height: Int
)