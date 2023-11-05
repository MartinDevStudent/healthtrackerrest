package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object MealsIngredients : Table("meals_ingredients") {
    val id = integer("id").autoIncrement().primaryKey()
    val meal = reference("meal_id", Meals, onDelete = ReferenceOption.CASCADE)
    val ingredient = reference("ingredient_id", Ingredients, onDelete = ReferenceOption.CASCADE)
}
