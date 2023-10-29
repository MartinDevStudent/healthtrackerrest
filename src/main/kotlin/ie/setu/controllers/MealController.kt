package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.MealRequestDTO
import ie.setu.domain.repository.IngredientDAO
import ie.setu.domain.repository.MealDAO
import ie.setu.infrastructure.NutrientHttpClient
import io.javalin.http.Context
import mapObjectWithDateToJson

object MealController {
    private val mealDao = MealDAO()
    private val ingredientDao = IngredientDAO()

    /**
     * Retrieves and returns a list of all meals as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getAllMeals(ctx: Context) {
        ctx.json(mealDao.getAll())
    }

    /**
     * Retrieves and returns a meal by its unique meal ID as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getMealByMealId(ctx: Context) {
        val meal = mealDao.findById(ctx.pathParam("meal-id").toInt())
        if (meal != null) {
            ctx.json(meal)
        }
    }

    fun getIngredientsByMealId(ctx: Context) {
        val ingredients = ingredientDao.findByMealId(ctx.pathParam("meal-id").toInt())
        if (ingredients.isNotEmpty()) {
            ctx.json(mapObjectWithDateToJson(ingredients))
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    /**
     * Adds a new meal to the database based on the provided MealRequest and updates its food items.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun addMeal(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val meal = mapper.readValue<MealRequestDTO>(ctx.body())
        val ingredientDTOs = NutrientHttpClient.get(meal.name)

        if (ingredientDTOs.isNotEmpty()) {
            val mealId = mealDao.save(meal.name)
            ingredientDTOs.forEach { ingredientDao.save(mealId, it) }
        }
    }
}
