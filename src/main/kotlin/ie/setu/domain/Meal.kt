package ie.setu.domain

data class Meal(
    val id: Int,
    val name: String,
    val calories: Double,
    val servingSizeG: Double,
    val fatTotalG: Double,
    val fatSaturatedG: Double,
    val proteinG: Double,
    val sodiumMg: Int,
    val potassiumMg: Int,
    val cholesterolMg: Int,
    val carbohydratesTotalG: Double,
    val fiberG: Double,
    val sugarG: Double,
)