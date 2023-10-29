package ie.setu.domain.repository

import ie.setu.domain.Ingredient
import ie.setu.domain.IngredientApiDTO
import kotlin.collections.ArrayList

class IngredientDAO {
    private val ingredients = arrayListOf(
        Ingredient(
            id = 1,
            name = "burger",
            calories = 237.7,
            servingSizeG = 100.0,
            fatTotalG = 11.5,
            fatSaturatedG = 4.7,
            proteinG = 15.2,
            sodiumMg = 356,
            potassiumMg = 137,
            cholesterolMg = 53,
            carbohydratesTotalG = 18.1,
            fiberG = 0.0,
            sugarG = 0.0,
            mealId = 1
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
            sugarG = 0.3,
            mealId = 1
        ),
        Ingredient(
            id = 3,
            name = "salmon",
            calories = 208.7,
            servingSizeG = 100.0,
            fatTotalG = 12.1,
            fatSaturatedG = 2.4,
            proteinG = 22.0,
            sodiumMg = 61,
            potassiumMg = 253,
            cholesterolMg = 63,
            carbohydratesTotalG = 0.0,
            fiberG = 0.0,
            sugarG = 0.0,
            mealId = 2
        ),
        Ingredient(
            id = 4,
            name = "salad",
            calories = 23.6,
            servingSizeG = 100.0,
            fatTotalG = 0.2,
            fatSaturatedG = 0.0,
            proteinG = 1.5,
            sodiumMg = 36,
            potassiumMg = 32,
            cholesterolMg = 0,
            carbohydratesTotalG = 4.9,
            fiberG = 1.9,
            sugarG = 2.2,
            mealId = 2
        )
    )

    fun getAll(): ArrayList<Ingredient>  {
        return ingredients
    }

    fun findById(id: Int): Ingredient? {
        return ingredients.find { it.id == id }
    }

    fun findByMealId(mealId: Int): List<Ingredient> {
        return ingredients.filter { it.mealId == mealId }
    }

    fun save (mealId: Int, dto: IngredientApiDTO) {
        val id = ingredients.last().id + 1
        ingredients.add(
            Ingredient(
                id = id,
                name = dto.name,
                calories = dto.calories,
                servingSizeG = dto.servingSizeG,
                fatTotalG = dto.fatTotalG,
                fatSaturatedG = dto.fatSaturatedG,
                proteinG = dto.proteinG,
                sodiumMg = dto.sodiumMg,
                potassiumMg = dto.potassiumMg,
                cholesterolMg = dto.cholesterolMg,
                carbohydratesTotalG = dto.carbohydratesTotalG,
                fiberG = dto.fiberG,
                sugarG = dto.sugarG,
                mealId = mealId
            )
        )
    }
}
