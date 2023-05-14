package cz.milancu.app.beunlost.domain.model.entity

import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.*

@Entity
class FolderAccess(
    @Id
    var id: UUID = UUID.randomUUID(),
    var folderId: UUID,
    var userId: UUID,
    @Enumerated(EnumType.STRING)
    var accessType: FolderAccessType,
    var deleted: Boolean = false
)