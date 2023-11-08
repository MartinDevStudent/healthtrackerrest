package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Meal
import ie.setu.domain.user.User
import ie.setu.helpers.INVALID_MEAL_NAME
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
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
class MealControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class ReadMeals {
        @Test
        fun `get all meals from the database returns 200 or 404 response`() {
            val response = Unirest.get("$origin/api/meals/").asString()
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
            val retrieveResponse = retrieveMealById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `get meal by id when meal exists returns 200 response`() {
            // Arrange
            // add a meal to the database
            val addMealResponse = addMeal(validMealName)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act - retrieve the meal from the database
            val retrieveResponse = retrieveMealById(addedMeal.id)

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `getting meals by user id when user doesnt exist returns 404 response`() {
            // Arrange - test data for meal id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent meal from the database
            val retrieveResponse = retrieveMealByUserId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting meals by user id when user exist returns 200 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = addMealByUserId(validMealName, addedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act - retrieve the meal from the database via the user id
            val retrieveResponse = retrieveMealByUserId(addedUser.id)

            // Assert -  verify return code
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added user and meal
            val deleteMealResponse = deleteMeal(addedMeal.id)
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
            assertEquals(204, deleteMealResponse.status)
        }
    }

    @Nested
    inner class CreateMeals {
        @Test
        fun `adding a meal with an invalid name, returns a 400 response`() {
            // Act - add a meal to the database
            val addMealResponse = addMeal(INVALID_MEAL_NAME)

            // Assert -  verify return code
            assertEquals(400, addMealResponse.status)
        }

        @Test
        fun `adding a meal when it already exists, returns a 409 response`() {
            // Arrange - add a meal to the database
            val addMealResponse = addMeal(validMealName)

            // Act - attempt to add second meal with same name
            val secondAddMealResponse = addMeal(validMealName)

            // Assert - verify return code
            assertEquals(409, secondAddMealResponse.status)

            // After - restore the db to previous state by deleting the original meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `add a meal with correct details returns a 201 response`() {
            // Act - add a meal to the database
            val addMealResponse = addMeal(validMealName)

            // Assert -  verify return code
            assertEquals(201, addMealResponse.status)

            // After - restore the db to previous state by deleting the added meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `adding a meal with an invalid name that is associated with a user, returns a 400 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Act - attempt to add meal associated with user that has an invalid name
            val addMealResponse = addMealByUserId(INVALID_MEAL_NAME, addedUser.id)

            // Assert -  verify return code
            assertEquals(400, addMealResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `associating an already existing meal with a user, returns a 201 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = addMeal(validMealName)

            // Act - associate the same meal with a user
            val addUserMealResponse = addMealByUserId(validMealName, addedUser.id)

            // Assert - verify return code
            assertEquals(201, addUserMealResponse.status)

            // After - restore the db to previous state by deleting the added user and meal
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = deleteMeal(addedMeal.id)
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteMealResponse.status)
            assertEquals(204, deleteUserResponse.status)
        }
    }

    @Nested
    inner class DeleteMeals {
        @Test
        fun `deleting a meal when it doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an meal that doesn't exist
            assertEquals(404, deleteMeal(-1).status)
        }

        @Test
        fun `deleting a meal when it exists, returns a 204 response`() {
            // Arrange - add the meal that we plan to do a delete on
            val addMealResponse = addMeal(validMealName)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act & Assert - delete the added meal and assert a 204 is returned
            assertEquals(204, deleteMeal(addedMeal.id).status)

            // Act & Assert - attempt to retrieve the deleted meal --> 404 response
            assertEquals(404, retrieveMealById(addedMeal.id).status)
        }

        @Test
        fun `deleting meals by user when user id doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, deleteMealsByUserId(-1).status)
        }

        @Test
        fun `deleting meals by user when user id exists, returns a 204 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = addMealByUserId(validMealName, addedUser.id)

            // Act & Assert - delete the activity and verify status
            assertEquals(204, deleteMealsByUserId(addedUser.id).status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = deleteUser(addedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteUserResponse.status)
            assertEquals(204, deleteMealResponse.status)
        }

        @Test
        fun `deleting user meal by meal id when meal id doesn't exist, returns a 404 response`() {
            // Arrange
            // add a user to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, deleteUserMealByMealId(addedUser.id, -1).status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun ` deleting user meal by meal id when meal id exists, returns a 204 response`() {
            // Arrange
            // add a user and meal to the database
            val addUserResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addMealResponse = addMealByUserId(validMealName, addedUser.id)
            val addedMeal: Meal = jsonToObject(addMealResponse.body.toString())

            // Act & Assert - delete the activity and verify status
            assertEquals(204, deleteUserMealByMealId(addedUser.id, addedMeal.id).status)

            // After - restore the db to previous state by deleting the added user and meal
            val deleteUserResponse = deleteUser(addedUser.id)
            val deleteMealResponse = deleteMeal(addedMeal.id)
            assertEquals(204, deleteUserResponse.status)
            assertEquals(204, deleteMealResponse.status)
        }
    }

    // helper function to retrieve a test meal from the database by meal id
    private fun retrieveMealById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id").asString()
    }

    // helper function to retrieve a test meal from the database by meal id
    private fun retrieveMealByUserId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$id/meals").asString()
    }

    // helper function to add a test meal to the database
    private fun addMeal(name: String): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    // helper function to add a test meal associated with a user to the database
    private fun addMealByUserId(
        name: String,
        userId: Int,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users/$userId/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    // helper function to delete a test meal from the database
    private fun deleteMeal(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/meals/$id").asString()
    }

    // helper function to delete all meals associated with a user from the database
    private fun deleteMealsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id/meals").asString()
    }

    // helper function to delete a meal associated with a user from the database
    private fun deleteUserMealByMealId(
        userId: Int,
        mealId: Int,
    ): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$userId/meals/$mealId").asString()
    }

    // helper function to add a test user to the database
    private fun addUser(
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    // helper function to delete a test user from the database
    private fun deleteUser(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id").asString()
    }
}