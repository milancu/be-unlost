package cz.milancu.app.beunlost.domain.model.entity

import cz.milancu.app.beunlost.domain.model.enum.FolderAccessType
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Where
import java.time.Instant
import java.util.*

@Entity
@Where(clause = "deleted=false")
class Folder(
    @Id
    var id: UUID = UUID.randomUUID(),
    var name: String,
    var deleted: Boolean = false,
    var createBy: UUID,
    var createAt: Instant = Instant.now(),
    var documentIds: MutableList<UUID> = ArrayList(),
    @Enumerated(EnumType.STRING)
    var folderType: FolderAccessType,

    @ManyToMany
    @JoinTable(
        name = "FOLDER_FOLDER_ACCESS",
        joinColumns = [JoinColumn(name = "FOLDER_ID")],
        inverseJoinColumns = [JoinColumn(name = "ACCESS_ID")]
    )
    @Fetch(FetchMode.JOIN)
    var folderAccesses: MutableList<FolderAccess> = ArrayList(),

    var customSchemaId: UUID? = null
)