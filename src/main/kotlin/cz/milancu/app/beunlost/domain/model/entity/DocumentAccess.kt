package cz.milancu.app.beunlost.domain.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class DocumentAccess(
    @Id
    var id: UUID = UUID.randomUUID(),
    var documentId: UUID,
    var userId: UUID
) {
}