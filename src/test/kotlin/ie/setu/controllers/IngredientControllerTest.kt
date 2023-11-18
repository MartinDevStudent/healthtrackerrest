package ie.setu.controllers

import ie.setu.domain.Ingredient
import ie.setu.domain.Meal
import ie.setu.domain.user.User
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_MEAL_NAME
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IngredientControllerTest {
    private val requests = IntegrationTestHelper()

    @BeforeEach
    fun ensureUserDoesNotExist() {
        val response = requests.retrieveUserByEmail(VALID_EMAIL)

        if (response.status == 200) {
            val retrievedUser: User = jsonToObject(response.body.toString())
            requests.deleteUser(retrievedUser.id)
        }
    }

    @Nested
    inner class ReadIngredients {
        @Test
        fun `getting all ingredients from the database returns 200 or 404 response`() {
            val response = requests.retrieveIngredients()
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
            val retrieveResponse = requests.retrieveIngredientById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting an ingredient by id when id exists, returns a 200 response`() {
            // Arrange - add the meal to retrieve ingredient
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val addedIngredientsResponse = requests.retrieveIngredientByMealId(addedMeal.id)
            val addedIngredients: ArrayList<Ingredient> = jsonToObject(addedIngredientsResponse.body.toString())

            // Assert - retrieve the added ingredient from the database and verify return code
            val retrieveResponse = requests.retrieveIngredientById(addedIngredients[0].id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = requests.deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal does not exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the ingredients using the non-existent meal from the database
            val retrieveResponse = requests.retrieveIngredientByMealId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal exists, returns a 200 response`() {
            // Arrange - add the meal and ingredients
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Assert - retrieve the ingredients from the database and verify return code
            val retrieveResponse = requests.retrieveIngredientByMealId(addedMeal.id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = requests.deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting recommended daily allowances, returns a 200 response`() {
            // Act - attempt to retrieve the ingredients using the non-existent meal from the database
            val retrieveResponse = requests.retrieveRecommendedDailyAllowances()

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)
        }
    }
}
