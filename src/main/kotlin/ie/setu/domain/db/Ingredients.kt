package ie.setu.domain.db

import org.jetbrains.exposed.dao.IntIdTable

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object Ingredients : IntIdTable("ingredients") {
    val name = varchar("name", 100)
    val calories = double("calories")
    val servingSizeG = double("serving_size_g")
    val fatTotalG = double("fat_total_g")
    val fatSaturatedG = double("fat_saturated_g")
    val proteinG = double("protein_g")
    val sodiumMg = integer("sodium_mg")
    val potassiumMg = integer("potassium_mg")
    val cholesterolMg = integer("cholesterol_mg")
    val carbohydratesTotalG = double("carbohydrates_total_g")
    val fiberG = double("fiber_g")
    val sugarG = double("sugar_g")
}