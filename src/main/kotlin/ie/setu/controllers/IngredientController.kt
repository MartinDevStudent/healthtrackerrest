package ie.setu.controllers

import ie.setu.domain.repository.IngredientDAO
import ie.setu.domain.repository.RecommendedDailyAllowancesDAO
import io.javalin.http.Context

/**
 * Singleton object for handling ingredient-related operations.
 *
 * This object provides functionality for retrieving information about ingredients and recommended daily allowances.
 */
object IngredientController {
    private val ingredientDao = IngredientDAO()
    private val recommendedDailyAllowancesDao = RecommendedDailyAllowancesDAO()

    /**
     * Retrieves a list of all ingredients and sends them as a response in JSON format.
     *
     * This method retrieves a list of all ingredients from the system, and if any ingredients are found,
     * it responds with a status code 200 (OK) and sends the ingredients in JSON format as the response.
     * If there are no ingredients in the system, it responds with a status code 404 (Not Found).
     *
     * @param ctx The context object containing request and response information.
     */
    fun getAllIngredients(ctx: Context) {
        val ingredients = ingredientDao.getAll()
        if (ingredients.size != 0) {
            ctx.json(ingredients)
            ctx.status(200)
        } else {
            ctx.status(404)
        }
    }

    /**
     * Retrieves an ingredient by its unique identifier (ingredient ID) and sends it as a response.
     *
     * This method retrieves an ingredient from the system by its unique identifier (ingredient ID) and,
     * if found, responds with a status code 200 (OK) and sends the ingredient as the response in JSON
     * format. If no matching ingredient is found, it responds with a status code 404 (Not Found).
     *
     * @param ctx The context object containing the ingredient ID as a path parameter.
     */
    fun getIngredientByIngredientId(ctx: Context) {
        val ingredient = ingredientDao.findByIngredientId(ctx.pathParam("ingredient-id").toInt())
        if (ingredient != null) {
            ctx.json(ingredient)
            ctx.status(200)
        } else {
            ctx.status(404)
        }
    }

    /**
     * Retrieves a list of recommended daily allowances (RDAs) and sends it as a JSON response.
     *
     * @param ctx The Javalin context object representing the HTTP request and response.
     */
    fun getRecommendedDailyAllowances(ctx: Context) {
        val recommendedDailyAllowances = recommendedDailyAllowancesDao.get()
        if (recommendedDailyAllowances != null) {
            ctx.json(recommendedDailyAllowances)
            ctx.status(200)
        } else {
            ctx.status(404)
        }
    }
}
