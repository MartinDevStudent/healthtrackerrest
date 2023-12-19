package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Meal
import ie.setu.domain.user.User
import ie.setu.helpers.INVALID_MEAL_NAME
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
class MealControllerTest {
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
    inner class ReadMeals {
        @Test
        fun `get all meals from the database returns 200 or 404 response`() {
            val response = requests.retrieveMeals(jwtToken)
            if (response.status == 200) {
                val retrievedMeals: ArrayList<Meal> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedMeals.size)
            } else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get meals by id when meal does not exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent meal from the database
            val retrieveResponse = requests.retrieveMealById(id, jwtToken)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `get meal by id when meal exists returns 200 response`() {
            // Arrange
            // add a meal to the database
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act - retrieve the meal from the database
            val retrieveResponse = requests.retrieveMealById(addedMeal.id, jwtToken)

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting meals by user id when user doesnt exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent meal from the database
            val retrieveResponse = requests.retrieveMealByUserId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting meals by user id when user exist returns 200 response`() {
            // Arrange - retrieve user and add meal to the database
            val retrievedUserResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            val retrievedUser: User = jsonToObject(retrievedUserResponse.body.toString())
            val addMealResponse = requests.addMealByUserId(VALID_MEAL_NAME, retrievedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act - retrieve the meal from the database via the user id
            val retrieveResponse = requests.retrieveMealByUserId(retrievedUser.id)

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added user and meal
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteMealResponse.status)
        }
    }

    @Nested
    inner class CreateMeals {
        @Test
        fun `adding a meal with an invalid name, returns a 400 response`() {
            // Act - add a meal to the database
            val addMealResponse = requests.addMeal(INVALID_MEAL_NAME, jwtToken)

            // Assert -  verify return code
            assertEquals(400, addMealResponse.status)
        }

        @Test
        fun `adding a meal when it already exists, returns a 409 response`() {
            // Arrange - add a meal to the database
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)

            // Act - attempt to add second meal with same name
            val secondAddMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)

            // Assert - verify return code
            assertEquals(409, secondAddMealResponse.status)

            // After - restore the db to previous state by deleting the original meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `add a meal with correct details returns a 201 response`() {
            // Act - add a meal to the database
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)

            // Assert -  verify return code
            assertEquals(201, addMealResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `adding a meal with an invalid name that is associated with a user, returns a 400 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Act - attempt to add meal associated with user that has an invalid name
            val addMealResponse = requests.addMealByUserId(INVALID_MEAL_NAME, addedUser.id)

            // Assert -  verify return code
            assertEquals(400, addMealResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `associating an already existing meal with a user, returns a 201 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)

            // Act - associate the same meal with a user
            val addUserMealResponse = requests.addMealByUserId(VALID_MEAL_NAME, addedUser.id)

            // Assert - verify return code
            assertEquals(201, addUserMealResponse.status)

            // After - restore the db to previous state by deleting the added user and meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteMealResponse.status)
            assertEquals(204, deleteUserResponse.status)
        }
    }

    @Nested
    inner class DeleteMeals {
        @Test
        fun `deleting a meal when it doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an meal that doesn't exist
            assertEquals(404, requests.deleteMeal(Integer.MIN_VALUE, jwtToken).status)
        }

        @Test
        fun `deleting a meal when it exists, returns a 204 response`() {
            // Arrange - add the meal that we plan to do a delete on
            val addMealResponse = requests.addMeal(VALID_MEAL_NAME, jwtToken)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act & Assert - delete the added meal and assert a 204 is returned
            assertEquals(204, requests.deleteMeal(addedMeal.id, jwtToken).status)

            // Act & Assert - attempt to retrieve the deleted meal --> 404 response
            assertEquals(404, requests.retrieveMealById(addedMeal.id, jwtToken).status)
        }

        @Test
        fun `deleting meals by user when user id doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, requests.deleteMealsByUserId(Integer.MIN_VALUE).status)
        }

        @Test
        fun `deleting meals by user when user id exists, returns a 204 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = requests.addMealByUserId(VALID_MEAL_NAME, addedUser.id)

            // Act & Assert - delete the activity and verify status
            assertEquals(204, requests.deleteMealsByUserId(addedUser.id).status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteUserResponse.status)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `deleting user meal by meal id when meal id doesn't exist, returns a 404 response`() {
            // Arrange
            // add a user to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, requests.deleteUserMealByMealId(addedUser.id, Integer.MIN_VALUE).status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun ` deleting user meal by meal id when meal id exists, returns a 204 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = requests.addMealByUserId(VALID_MEAL_NAME, addedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act & Assert - delete the activity and verify status
            assertEquals(204, requests.deleteUserMealByMealId(addedUser.id, addedMeal.id).status)

            // After - restore the db to previous state by deleting the added user and meal
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            val deleteMealResponse = requests.deleteMeal(addedMeal.id, jwtToken)
            assertEquals(204, deleteUserResponse.status)
            assertEquals(204, deleteMealResponse.status)
        }
    }
}
