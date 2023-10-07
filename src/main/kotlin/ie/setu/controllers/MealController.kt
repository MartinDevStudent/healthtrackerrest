package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.MealRequest
import ie.setu.domain.repository.MealDAO
import ie.setu.infrastructure.NutrientHttpClient
import io.javalin.http.Context

object MealController {
    private val mealDao = MealDAO()

    fun getAllMeals(ctx: Context) {
        ctx.json(mealDao.getAll())
    }

    fun getMealById(ctx: Context) {
        val meal = mealDao.findById(ctx.pathParam("meal-id").toInt())
        if (meal != null) {
            ctx.json(meal)
        }
    }

    fun addMeal(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val meal = mapper.readValue<MealRequest>(ctx.body())
        NutrientHttpClient.get(meal.name)
        ctx.json(meal)
    }
}
