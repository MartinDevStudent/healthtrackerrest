package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object UsersMeals : Table("users_meals") {
    val id = integer("id").autoIncrement().primaryKey()
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val meal = reference("meal_id", Meals, onDelete = ReferenceOption.CASCADE)
}