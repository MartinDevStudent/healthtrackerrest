package ie.setu.domain.db

import org.jetbrains.exposed.dao.IntIdTable

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object Users : IntIdTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 255)
    val level = varchar("level", 100)
    val passwordHash = varchar("password_hash", 255)
}
