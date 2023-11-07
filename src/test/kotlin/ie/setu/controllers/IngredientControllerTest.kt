package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Ingredient
import ie.setu.domain.Meal
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.validMealName
import jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IngredientControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class ReadIngredients {
        @Test
        fun `getting all ingredients from the database returns 200 or 404 response`() {
            val response = Unirest.get("$origin/api/ingredients/").asString()
            if (response.status == 200) {
                val retrievedIngredients: ArrayList<Ingredient> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedIngredients.size)
            } else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `getting ingredient by id when ingredient does not exist returns 404 response`() {
            // Arrange - test data for ingredient id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent ingredient from the database
            val retrieveResponse = retrieveIngredientById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting an ingredient by id when id exists, returns a 200 response`() {
            // Arrange - add the meal to retrieve ingredient
            val addMealResponse = addMeal(validMealName)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val addedIngredientsResponse = retrieveIngredientByMealId(addedMeal.id)
            val addedIngredients: ArrayList<Ingredient> = jsonToObject(addedIngredientsResponse.body.toString())

            // Assert - retrieve the added ingredient from the database and verify return code
            val retrieveResponse = retrieveIngredientById(addedIngredients[0].id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal does not exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the ingredients using the non-existent meal from the database
            val retrieveResponse = retrieveIngredientByMealId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal exists, returns a 200 response`() {
            // Arrange - add the meal and ingredients
            val addMealResponse = addMeal(validMealName)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Assert - retrieve the ingredients from the database and verify return code
            val retrieveResponse = retrieveIngredientByMealId(addedMeal.id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting recommended daily allowances, returns a 200 response`() {
            // Act - attempt to retrieve the ingredients using the non-existent meal from the database
            val retrieveResponse = retrieveRecommendedDailyAllowances()

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)
        }
    }

    // helper function to retrieve a test ingredient from the database by id
    private fun retrieveIngredientById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/$id").asString()
    }

    // helper function to retrieve ingredients from the database by meal id
    private fun retrieveIngredientByMealId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id/ingredients").asString()
    }

    // helper function to retrieve recommended daily allowances from the database
    private fun retrieveRecommendedDailyAllowances(): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/rda").asString()
    }

    // helper function to add a test meal to the database
    private fun addMeal(name: String): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    // helper function to delete a test meal from the database
    private fun deleteMeal(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/meals/$id").asString()
    }
}
