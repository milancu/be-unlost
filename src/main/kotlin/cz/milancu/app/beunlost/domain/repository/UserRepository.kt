package cz.milancu.app.beunlost.domain.repository

import cz.milancu.app.beunlost.domain.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findById(id: UUID): User?
}