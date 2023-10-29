package ie.setu.domain.repository

import ie.setu.domain.Meal
import kotlin.collections.ArrayList

class MealDAO {
    private val meals = arrayListOf<Meal>(
        Meal(
            id = 1,
            name = "Burger and fries",
        ),
        Meal(
            id = 2,
            name = "Salmon and salad",
        )
    )

    fun getAll(): ArrayList<Meal>  {
        return meals
    }

    fun findById(id: Int): Meal? {
        return meals.find { it.id == id }
    }

    fun save (mealName: String): Int {
        val id = meals.last().id + 1
        meals.add(Meal(id, mealName))
        return id
    }
}
