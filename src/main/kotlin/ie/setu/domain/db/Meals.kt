package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object Meals : Table("meals") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
}