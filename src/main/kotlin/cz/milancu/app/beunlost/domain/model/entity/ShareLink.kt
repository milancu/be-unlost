package cz.milancu.app.beunlost.domain.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant
import java.util.*

@Entity
class ShareLink(
    @Id
    var id: UUID = UUID.randomUUID(),
    var link: String,
    var lifetime: Instant = Instant.now().plusSeconds(600),
    var documentId: UUID,
    var createBy: UUID,
    var createAt: Instant = Instant.now()
) {
}