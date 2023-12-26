package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Ingredient
import ie.setu.domain.Meal
import ie.setu.domain.user.User
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_MEAL_NAME
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import ie.setu.utils.authentication.JwtDTO
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IngredientControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    private val requests = IntegrationTestHelper(origin)
    private var jwtToken: String = ""

    @BeforeEach
    fun createTestUser() {
        val response = requests.retrieveUserByEmail(VALID_EMAIL)

        if (response.status == 200) {
            val retrievedUser: User = jsonToObject(response.body.toString())
            requests.deleteUser(retrievedUser.id)
        }

        requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
    }

    @BeforeEach
    fun fetchAuthenticationToken() {
        val loginResponse = requests.login(VALID_EMAIL, VALID_PASSWORD)
        val jwtDTO: JwtDTO = jsonToObject(loginResponse.body.toString())

        jwtToken = jwtDTO.jwt
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
            // Arrange - add/ retrieve the meal to retrieve ingredient
            val allMealsResponse = requests.retrieveMeals(jwtToken)
            val allMeals: List<Meal> = jsonToObject(allMealsResponse.body.toString())

            val meal =
                if (allMeals.any { x -> x.name == VALID_MEAL_NAME }) {
                    allMeals.first { x -> x.name == VALID_MEAL_NAME }
                } else {
                    val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)
                    jsonToObject(addMealResponse.body.toString())
                }

            val addedIngredientsResponse = requests.retrieveIngredientByMealId(meal.id, jwtToken)
            val addedIngredients: ArrayList<Ingredient> = jsonToObject(addedIngredientsResponse.body.toString())

            // Assert - retrieve the added ingredient from the database and verify return code
            val retrieveResponse = requests.retrieveIngredientById(addedIngredients[0].id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = requests.deleteMeal(meal.id, jwtToken)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal does not exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the ingredients using the non-existent meal from the database
            val retrieveResponse = requests.retrieveIngredientByMealId(id, jwtToken)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting ingredients by meal id when meal exists, returns a 200 response`() {
            // Arrange - add the meal and ingredients
            val allMealsResponse = requests.retrieveMeals(jwtToken)
            val allMeals: List<Meal> = jsonToObject(allMealsResponse.body.toString())

            val meal =
                if (allMeals.any { x -> x.name == VALID_MEAL_NAME }) {
                    allMeals.first { x -> x.name == VALID_MEAL_NAME }
                } else {
                    val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)
                    jsonToObject(addMealResponse.body.toString())
                }

            // Assert - retrieve the ingredients from the database and verify return code
            val retrieveResponse = requests.retrieveIngredientByMealId(meal.id, jwtToken)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = requests.deleteMeal(meal.id, jwtToken)
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
