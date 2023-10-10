package ie.setu.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class FoodItem(
    val id: Int,
    val name: String,
    val calories: Double,
    @JsonProperty("serving_size_g")
    val servingSizeG: Double,
    @JsonProperty("fat_total_g")
    val fatTotalG: Double,
    @JsonProperty("fat_saturated_g")
    val fatSaturatedG: Double,
    @JsonProperty("protein_g")
    val proteinG: Double,
    @JsonProperty("sodium_mg")
    val sodiumMg: Int,
    @JsonProperty("potassium_mg")
    val potassiumMg: Int,
    @JsonProperty("cholesterol_mg")
    val cholesterolMg: Int,
    @JsonProperty("carbohydrates_total_g")
    val carbohydratesTotalG: Double,
    @JsonProperty("fiber_g")
    val fiberG: Double,
    @JsonProperty("sugar_g")
    val sugarG: Double,
)
