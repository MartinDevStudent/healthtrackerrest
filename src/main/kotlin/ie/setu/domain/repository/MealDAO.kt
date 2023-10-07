package ie.setu.domain.repository

import ie.setu.domain.Ingredient
import kotlin.collections.ArrayList

class MealDAO {
    private val meals = arrayListOf<Ingredient>(
        Ingredient(
            id = 1,
            name = "brisket",
            calories = 1312.3,
            servingSizeG = 453.592,
            fatTotalG = 82.9,
            fatSaturatedG = 33.2,
            proteinG = 132.0,
            sodiumMg = 217,
            potassiumMg = 781,
            cholesterolMg = 487,
            carbohydratesTotalG = 0.0,
            fiberG = 0.0,
            sugarG = 0.0
        ),
        Ingredient(
            id = 2,
            name = "fries",
            calories = 317.7,
            servingSizeG = 100.0,
            fatTotalG = 14.8,
            fatSaturatedG = 2.3,
            proteinG = 3.4,
            sodiumMg = 212,
            potassiumMg = 124,
            cholesterolMg = 0,
            carbohydratesTotalG = 41.1,
            fiberG = 3.8,
            sugarG = 0.3
        )
    )

    fun getAll(): ArrayList<Ingredient>  {
        return meals
    }

    fun findById(id: Int): Ingredient? {
        return meals.find { it.id == id }
    }
}
