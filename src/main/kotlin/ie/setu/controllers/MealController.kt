package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.MealRequest
import ie.setu.domain.repository.FoodItemDAO
import ie.setu.infrastructure.NutrientHttpClient
import io.javalin.http.Context

object MealController {
    private val foodItemDao = FoodItemDAO()

    /**
     * Retrieves and returns a list of all meals as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getAllMeals(ctx: Context) {
        ctx.json(foodItemDao.getAll())
    }

    /**
     * Retrieves and returns a meal by its unique meal ID as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getMealById(ctx: Context) {
        val meal = foodItemDao.findById(ctx.pathParam("meal-id").toInt())
        if (meal != null) {
            ctx.json(meal)
        }
    }

    /**
     * Adds a new meal to the database based on the provided MealRequest and updates its food items.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun addMeal(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val meal = mapper.readValue<MealRequest>(ctx.body())
        val food_items = NutrientHttpClient.get(meal.name)

        if (food_items.isNotEmpty()) {
            food_items.forEach { foodItemDao.save(it) }
        }
    }
}
