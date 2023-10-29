package ie.setu.controllers

import ie.setu.domain.repository.IngredientDAO
import io.javalin.http.Context

object IngredientController {
    private val ingredientDao = IngredientDAO()

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
        val ingredients =  ingredientDao.getAll()
        if (ingredients.size != 0) {
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
        ctx.json(ingredients)
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
        }
        else {
            ctx.status(404)
        }
    }
}
