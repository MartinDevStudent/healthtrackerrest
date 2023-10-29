package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Meal
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
        val meal = mealDao.findByMealId(ctx.pathParam("meal-id").toInt())
        if (meal != null) {
            ctx.json(meal)
        }
    }

    /**
     * Retrieves a list of ingredients by meal ID and returns them in JSON format.
     *
     * This method fetches ingredients associated with a specific meal ID and returns them as a JSON
     * response if any ingredients are found. If no ingredients are found for the given meal ID, it
     * responds with a status code 404 (Not Found).
     *
     * @param ctx The context object containing request and response information.
     */
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
     * Adds a new meal, along with its associated ingredients, to the system.
     *
     * This method is responsible for adding a new meal to the system. It expects a JSON representation
     * of the meal in the request body and processes it. The meal data is deserialized from the JSON
     * representation, and a request is made to fetch the ingredient data associated with the meal's name
     * from a remote service using NutrientHttpClient. If ingredient data is retrieved successfully,
     * the meal and its associated ingredients are saved in the database.
     *
     * @param ctx The context object containing the HTTP request body with the meal data.
     */
    fun addMeal(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val meal = mapper.readValue<Meal>(ctx.body())
        val ingredientDTOs = NutrientHttpClient.get(meal.name)

        if (ingredientDTOs.isNotEmpty()) {
            val mealId = mealDao.save(meal)
            ingredientDTOs.forEach { ingredientDao.save(mealId, it) }
        }
    }

    /**
     * Deletes a meal from the system based on its ID.
     *
     * This method deletes a meal from the system by its unique identifier (meal ID). If the meal is
     * successfully deleted, it responds with a status code 204 (No Content) to indicate success.
     * If the meal with the specified ID is not found in the system, it responds with a status code 404
     * (Not Found).
     *
     * @param ctx The context object containing the meal ID as a path parameter.
     */
    fun deleteMeal(ctx: Context) {
        if (mealDao.delete(ctx.pathParam("meal-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}
