package ie.setu.domain.db

import org.jetbrains.exposed.dao.IntIdTable

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object Meals : IntIdTable("meals") {
    val name = varchar("name", 100)
}
