package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one activity.
//       Database wise, this is the table object.

object RecommendedDailyAllowances : Table("recommended_daily_allowances") {
    val calories = double("calories")
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