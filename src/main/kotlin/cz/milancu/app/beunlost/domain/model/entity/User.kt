package cz.milancu.app.beunlost.domain.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Where
import java.util.*

@Entity
@Where(clause = "deleted=false")
@Table(name = "app_user")
class User(
    @Id
    var id: UUID = UUID.randomUUID(),
    var firstname: String,
    var lastname: String,
    var email: String,
    var deleted: Boolean = false,
    var imageUrl: String,

    @ManyToMany
    @JoinTable(
        name = "USER_DOCUMENT_ACCESS",
        joinColumns = [JoinColumn(name = "USER_ID")],
        inverseJoinColumns = [JoinColumn(name = "ACCESS_ID")]
    )
    @Fetch(FetchMode.JOIN)
    var documentAccesses: MutableList<DocumentAccess> = ArrayList(),

    @ManyToMany
    @JoinTable(
        name = "USER_FOLDER_ACCESS",
        joinColumns = [JoinColumn(name = "USER_ID")],
        inverseJoinColumns = [JoinColumn(name = "ACCESS_ID")]
    )
    @Fetch(FetchMode.JOIN)
    var folderAccesses: MutableList<FolderAccess> = ArrayList()
)