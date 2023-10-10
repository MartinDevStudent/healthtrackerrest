package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.MealRequest
import ie.setu.domain.repository.FoodItemDAO
import ie.setu.infrastructure.NutrientHttpClient
import io.javalin.http.Context

object MealController {
    private val foodItemDao = FoodItemDAO()

    fun getAllMeals(ctx: Context) {
        ctx.json(foodItemDao.getAll())
    }

    fun getMealById(ctx: Context) {
        val meal = foodItemDao.findById(ctx.pathParam("meal-id").toInt())
        if (meal != null) {
            ctx.json(meal)
        }
    }

    fun addMeal(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val meal = mapper.readValue<MealRequest>(ctx.body())
        val food_items = NutrientHttpClient.get(meal.name)

        if (food_items.isNotEmpty()) {
            food_items.forEach { foodItemDao.save(it) }
        }
    }
}
