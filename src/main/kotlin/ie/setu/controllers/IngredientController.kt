package ie.setu.controllers

import ie.setu.domain.repository.IngredientDAO
import io.javalin.http.Context

object IngredientController {
    private val ingredientDao = IngredientDAO()

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
