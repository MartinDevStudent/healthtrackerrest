package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.helpers.*
import jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateActivities {
        @Test
        fun `add an activity with correct details returns a 201 response`() {

            //Arrange
            //add a user to the database
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser : User = jsonToObject(addUserResponse.body.toString())

            //Arrange & Act & Assert
            //add the activity and verify return code (using fixture data)
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)
            val retrievedActivity : Activity = jsonToObject(addActivityResponse.body.toString())
            assertEquals(201, addActivityResponse.status)

            //Assert - retrieve the added activity from the database and verify return code
            val retrieveResponse= retrieveActivityById(retrievedActivity.id)
            assertEquals(200, retrieveResponse.status)

            //Assert - verify the contents of the retrieved activity
            assertEquals(validDescription, retrievedActivity.description)
            assertEquals(validDuration, retrievedActivity.duration)
            assertEquals(validCalories, retrievedActivity.calories)
            assertEquals(validStarted.toLocalDate(), retrievedActivity.started.toLocalDate())
            assertEquals(addedUser.id, retrievedActivity.userId)

            //After - restore the db to previous state by deleting the added user and activity
            val deleteActivityResponse = deleteActivity(retrievedActivity.id)
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteActivityResponse.status)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `add an activity when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            //Act
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, userId)

            //Assert
            assertEquals(404, addActivityResponse.status)
        }
    }

    @Nested
    inner class ReadActivities {
        @Test
        fun `get all activities from the database returns 200 or 404 response`() {
            val response = Unirest.get(origin + "/api/activities/").asString()
            if (response.status == 200) {
                val retrievedActivities: ArrayList<Activity> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedActivities.size)
            }
            else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get activity by id when activity does not exist returns 404 response`() {

            //Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = retrieveActivityById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting an activity when activity id exists, returns a 200 response`() {

            //Arrange - add the user and activity
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser : User = jsonToObject(addUserResponse.body.toString())
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)
            val addedActivity : Activity = jsonToObject(addActivityResponse.body.toString())

            //Assert - retrieve the user's activities from the database and verify return code
            val retrieveResponse = retrieveActivityById(addedActivity.id)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user and activity
            deleteActivity(addedActivity.id)
            deleteUser(addedUser.id)
        }

        @Test
        fun `get user's activities by id when user does not exist returns 404 response`() {

            //Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = retrieveActivitiesByUserId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user's activities when user id exists, returns a 200 response`() {

            //Arrange - add the user and activity
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser : User = jsonToObject(addUserResponse.body.toString())
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)

            //Assert - retrieve the user's activities from the database and verify return code
            val retrieveResponse = retrieveActivitiesByUserId(addedUser.id)
            assertEquals(200, retrieveResponse.status)

            //After - restore the db to previous state by deleting the added user and activity
            val addedActivity : Activity = jsonToObject(addActivityResponse.body.toString())
            deleteActivity(addedActivity.id)
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class UpdateActivities {
        @Test
        fun `updating a user when it exists, returns a 204 response`() {

            //Arrange - add the user that we plan to do an update on
            val updatedDescription = "Updated Description"
            val updatedDuration = 20.0
            val updatedCalories = 200
            val updatedStarted = DateTime.parse("2020-06-20T05:59:27.258Z")
            val updateUserResponse = addUser(validName, "updateduser@mail.com", validPassword)
            val updateUser: User = jsonToObject(updateUserResponse.body.toString())
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())
            val addActivityResponse =
                addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)
            val addedActivity: Activity = jsonToObject(addActivityResponse.body.toString())

            //Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(
                204,
                updateActivity(
                    addedActivity.id,
                    updatedDescription,
                    updatedDuration,
                    updatedCalories,
                    updatedStarted,
                    updateUser.id
                ).status
            )

            //Act & Assert - retrieve updated user and assert details are correct
            val updatedActivityResponse = retrieveActivityById(addedActivity.id)
            val updatedActivity: Activity = jsonToObject(updatedActivityResponse.body.toString())
            assertEquals(updatedDescription, updatedActivity.description)
            assertEquals(updatedDuration, updatedActivity.duration)
            assertEquals(updatedCalories, updatedActivity.calories)
            assertEquals(updatedStarted.toLocalDate(), updatedActivity.started.toLocalDate())
            assertEquals(updateUser.id, updatedActivity.userId)

            //After - restore the db to previous state by deleting the added user
            deleteActivity(addedActivity.id)
            deleteUser(addedUser.id)
            deleteUser(updateUser.id)
        }
    }

    @Nested
    inner class DeleteActivities {
        @Test
        fun `deleting an activity when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, deleteUser(-1).status)
        }

        @Test
        fun `deleting a activity when it exists, returns a 204 response`() {

            //Arrange - add the activity that we plan to do a delete on
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser : User = jsonToObject(addUserResponse.body.toString())
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)
            val addedActivity : Activity = jsonToObject(addActivityResponse.body.toString())

            //Act & Assert - delete the added activity and assert a 204 is returned
            assertEquals(204, deleteActivity(addedActivity.id).status)

            //Act & Assert - attempt to retrieve the deleted activity --> 404 response
            assertEquals(404, retrieveActivityById(addedActivity.id).status)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting an activity by user when user id doesn't exist, returns a 404 response`() {

            //Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, deleteUser(-1).status)
        }

        @Test
        fun `1deleting a activity when it exists, returns a 204 response`() {

            //Arrange - add the activity that we plan to do a delete on
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser : User = jsonToObject(addUserResponse.body.toString())
            val addActivityResponse = addActivity(validDescription, validDuration, validCalories, validStarted, addedUser.id)
            val addedActivity : Activity = jsonToObject(addActivityResponse.body.toString())

            //Act & Assert - delete the added activity and assert a 204 is returned
            assertEquals(204, deleteActivitiesByUserId(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted activity --> 404 response
            assertEquals(404, deleteActivitiesByUserId(addedUser.id).status)

            //After - restore the db to previous state by deleting the added user
            deleteUser(addedUser.id)
        }
    }

    //helper function to retrieve a test activity from the database by activity id
    private fun retrieveActivityById(id : Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/activities/${id}").asString()
    }

    //helper function to retrieve a test activity from the database by activity id
    private fun retrieveActivitiesByUserId(userId : Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${userId}/activities").asString()
    }

    //helper function to add a test activity to the database
    private fun addActivity(description: String, duration: Double, calories: Int, started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/activities")
            .body("{\"description\":\"$description\", \"duration\":\"$duration\", \"calories\":\"$calories\", \"started\":\"$started\", \"userId\":\"$userId\"}")
            .asJson()
    }

    //helper function to update a test activity to the database
    private fun updateActivity(id: Int, description: String, duration: Double, calories: Int, started: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/activities/$id")
            .body("{\"id\":\"$id\", \"description\":\"$description\", \"duration\":\"$duration\", \"calories\":\"$calories\", \"started\":\"$started\", \"userId\":\"$userId\"}")
            .asJson()
    }

    //helper function to delete a test activity from the database
    private fun deleteActivity (id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/activities/$id").asString()
    }

    private fun deleteActivitiesByUserId (userId: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/${userId}/activities").asString()
    }

    //helper function to retrieve a test user from the database by id
    private fun retrieveUserById(id: Int) : HttpResponse<String> {
        return Unirest.get(origin + "/api/users/${id}").asString()
    }

    //helper function to add a test user to the database
    private fun addUser (name: String, email: String, password: String): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    //helper function to delete a test user from the database
    private fun deleteUser (id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }
}