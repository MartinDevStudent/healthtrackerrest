package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Meal
import ie.setu.domain.repository.IngredientDAO
import ie.setu.domain.repository.MealDAO
import ie.setu.infrastructure.NutrientHttpClient
import io.javalin.http.BadRequestResponse
import io.javalin.http.ConflictResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import jsonToObject

object MealController {
    private val ingredientDao = IngredientDAO()
    private val mealDao = MealDAO()

    /**
     * Retrieves and returns a list of all meals as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getAll(ctx: Context) {
        val meals = mealDao.getAll()

        if (meals.isEmpty())
            throw NotFoundResponse("No meals found")

        ctx.json(meals)
        ctx.status(200)
    }

    /**
     * Retrieves and returns a meal by its unique meal ID as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getOne(ctx: Context) {
        val meal = mealDao.findByMealId(ctx.pathParam("meal-id").toInt())

        if (meal == null)
            throw NotFoundResponse("No meal with specified id found")

        ctx.json(meal)
        ctx.status(200)
    }

    /**
     * Retrieves a list of meals associated with a specific user ID and sends it as a JSON response.
     *
     * @param ctx The Javalin context object representing the HTTP request and response.
     */
    fun getByUserId(ctx: Context) {
        val meals = mealDao.findByUserId(ctx.pathParam("user-id").toInt())

        if (meals.isEmpty())
            throw NotFoundResponse("No meals associated with specified user found")

        ctx.json(meals)
        ctx.status(200)
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

        if (ingredients.isEmpty())
            throw NotFoundResponse("Meal with specified id not found")

        ctx.json(ingredients)
        ctx.status(200)
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
    fun create(ctx: Context) {
        //val mapper = jacksonObjectMapper()
        //val mealDto = mapper.readValue<Meal>(ctx.body())

        val mealDto: Meal = jsonToObject(ctx.body())
        val errorDetails = mealDto.validate()

        if (errorDetails.isNotEmpty())
            throw BadRequestResponse(message = "Invalid meal", errorDetails)

        var meal = mealDao.findByMealName(mealDto.name)

        if (meal != null)
            throw ConflictResponse("Meal already exists in database")

        val ingredients = NutrientHttpClient.get(mealDto.name)

        if (ingredients.isEmpty())
            throw BadRequestResponse()

        meal = Meal(
            id = mealDao.save(mealDto),
            name = mealDto.name,
        )

        ingredients.forEach {
            val ingredientId = ingredientDao.save(it)
            ingredientDao.associateIngredientWithMeal(ingredientId, meal.id)
        }

        ctx.json(meal)
        ctx.status(201)
    }

    /**
     * Adds a meal to a user's profile and associates it with the specified user ID.
     *
     * @param ctx The Javalin context object representing the HTTP request and response.
     * @throws NumberFormatException If the "user-id" path parameter cannot be converted to an integer.
     */
    fun createUserMeal(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val mapper = jacksonObjectMapper()
        val mealDto = mapper.readValue<Meal>(ctx.body())

        var meal = mealDao.findByMealName(mealDto.name)

        if (meal == null) {
            val ingredientDTOs = NutrientHttpClient.get(mealDto.name)

            if (ingredientDTOs.isEmpty())
                throw BadRequestResponse("Invalid meal name")

            meal = Meal(
                id = mealDao.save(mealDto),
                name = mealDto.name,
            )

            ingredientDTOs.forEach {
                val ingredientId = ingredientDao.save(it)
                ingredientDao.associateIngredientWithMeal(ingredientId, meal.id)
            }
        }

        mealDao.associateMealWithUser(userId, meal.id)
        ctx.json(meal)
        ctx.status(201)
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
    fun delete(ctx: Context) {
        if (mealDao.delete(ctx.pathParam("meal-id").toInt()) == 0)
            throw NotFoundResponse("No meal with specified id found")

        ctx.status(204)
    }

    /**
     * Deletes all meals associated with a specific user ID and updates the HTTP response status.
     *
     * @param ctx The Javalin context object representing the HTTP request and response.
     */
    fun deleteUserMealsByUserId(ctx: Context) {
        if (mealDao.deleteByUserId(ctx.pathParam("user-id").toInt()) == 0)
            throw NotFoundResponse("No user with specified id found or user does not have any meals")

        ctx.status(204)
    }

    /**
     * Deletes the association between a specific user and a meal by their respective IDs.
     *
     * @param ctx The Javalin context object representing the HTTP request and response.
     */
    fun deleteUserMealByMealId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val mealId = ctx.pathParam("meal-id").toInt()

        if (mealDao.deleteUserMealByMealId(userId, mealId) == 0)
            throw NotFoundResponse("No user with specified id or meal with specified id found")

        ctx.status(204)
    }
}
