package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity
import ie.setu.domain.user.JwtDTO
import ie.setu.domain.user.User
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.UPDATED_CALORIES
import ie.setu.helpers.UPDATED_DESCRIPTION
import ie.setu.helpers.UPDATED_DURATION
import ie.setu.helpers.UPDATED_EMAIL
import ie.setu.helpers.VALID_CALORIES
import ie.setu.helpers.VALID_DESCRIPTION
import ie.setu.helpers.VALID_DURATION
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import ie.setu.helpers.updatedStarted
import ie.setu.helpers.validStarted
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityControllerTest {
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
    inner class CreateActivities {
        @Test
        fun `adding an activity with correct details returns a 201 response`() {

            val response = requests.retrieveUserByEmail(VALID_EMAIL)
            val retrievedUser: User = jsonToObject(response.body.toString())

            // Arrange & Act & Assert
            // add the activity and verify return code (using fixture data)
            val addActivityResponse = requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, retrievedUser.id, jwtToken)
            val retrievedActivity: Activity = jsonToObject(addActivityResponse.body.toString())
            assertEquals(201, addActivityResponse.status)

            // Assert - retrieve the added activity from the database and verify return code
            val retrieveResponse = requests.retrieveActivityById(retrievedActivity.id, jwtToken)
            assertEquals(200, retrieveResponse.status)

            // Assert - verify the contents of the retrieved activity
            assertEquals(VALID_DESCRIPTION, retrievedActivity.description)
            assertEquals(VALID_DURATION, retrievedActivity.duration)
            assertEquals(VALID_CALORIES, retrievedActivity.calories)
            assertEquals(validStarted.toLocalDate(), retrievedActivity.started.toLocalDate())
            assertEquals(retrievedUser.id, retrievedActivity.userId)

            // After - restore the db to previous state by deleting the added user and activity
            val deleteActivityResponse = requests.deleteActivity(retrievedActivity.id, jwtToken)
            assertEquals(204, deleteActivityResponse.status)
        }

        @Test
        fun `adding an activity when no user exists for it, returns a 404 response`() {
            // Arrange - check there is no user for -1 id
            val userId = Integer.MIN_VALUE

            // Act
            val addActivityResponse = requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, userId, jwtToken)

            // Assert
            assertEquals(404, requests.retrieveUserById(userId).status)
            assertEquals(404, addActivityResponse.status)
        }
    }

    @Nested
    inner class ReadActivities {
        @Test
        fun `get all activities from the database returns 200 or 404 response`() {
            val response = requests.retrieveActivities(jwtToken)

            if (response.status == 200) {
                val retrievedActivities: ArrayList<Activity> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedActivities.size)
            } else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get activity by id when activity does not exist returns 404 response`() {
            // Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = requests.retrieveActivityById(id, jwtToken)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting an activity when activity id exists, returns a 200 response`() {
            // Arrange - retrieve the user and add the activity
            val retrievedUserResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            val retrievedUser: User = jsonToObject(retrievedUserResponse.body.toString())
            val addActivityResponse = requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, retrievedUser.id, jwtToken)
            val addedActivity: Activity = jsonToObject(addActivityResponse.body.toString())

            // Assert - retrieve the user's activities from the database and verify return code
            val retrieveResponse = requests.retrieveActivityById(addedActivity.id, jwtToken)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added activity
            requests.deleteActivity(addedActivity.id, jwtToken)
        }

        @Test
        fun `get user's activities by id when user does not exist returns 404 response`() {
            // Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = requests.retrieveActivitiesByUserId(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user's activities when user id exists, returns a 200 response`() {
            // Arrange - retrieve the user and activity
            val retrievedUserResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            val retrievedUser: User = jsonToObject(retrievedUserResponse.body.toString())
            val addActivityResponse = requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, retrievedUser.id, jwtToken)

            // Assert - retrieve the user's activities from the database and verify return code
            val retrieveResponse = requests.retrieveActivitiesByUserId(retrievedUser.id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the activity
            val addedActivity: Activity = jsonToObject(addActivityResponse.body.toString())
            requests.deleteActivity(addedActivity.id, jwtToken)
        }
    }

    @Nested
    inner class UpdateActivities {
        @Test
        fun `updating an activity when it exists, returns a 204 response`() {
            // Arrange - add the activity that we plan to do an update on and users associated with the activity
            val originalUserResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            val originalUser: User = jsonToObject(originalUserResponse.body.toString())
            val addActivityResponse =
                requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, originalUser.id, jwtToken)
            val addedActivity: Activity = jsonToObject(addActivityResponse.body.toString())

            var updateUserResponse = requests.retrieveUserByEmail(UPDATED_EMAIL)

            if (updateUserResponse.status != 200) {
                updateUserResponse = requests.addUser(VALID_NAME, UPDATED_EMAIL, VALID_PASSWORD)
            }

            val updateUser: User = jsonToObject(updateUserResponse.body.toString())
            val loginResponse = requests.login(UPDATED_EMAIL, VALID_PASSWORD)
            val jwtDTO: JwtDTO = jsonToObject(loginResponse.body.toString())

            // Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(
                204,
                requests.updateActivity(
                    addedActivity.id,
                    UPDATED_DESCRIPTION,
                    UPDATED_DURATION,
                    UPDATED_CALORIES,
                    updatedStarted,
                    updateUser.id,
                    jwtToken
                ).status,
            )

            // Act & Assert - retrieve updated user and assert details are correct
            val updatedActivityResponse = requests.retrieveActivityById(addedActivity.id, jwtDTO.jwt)
            val updatedActivity: Activity = jsonToObject(updatedActivityResponse.body.toString())
            assertEquals(UPDATED_DESCRIPTION, updatedActivity.description)
            assertEquals(UPDATED_DURATION, updatedActivity.duration)
            assertEquals(UPDATED_CALORIES, updatedActivity.calories)
            assertEquals(updatedStarted.toLocalDate(), updatedActivity.started.toLocalDate())
            assertEquals(updateUser.id, updatedActivity.userId)

            // After - restore the db to previous state by deleting the added activity and users
            requests.deleteActivity(addedActivity.id, jwtDTO.jwt)
            requests.deleteUser(updateUser.id)
        }

        @Test
        fun `updating an activity when it doesn't exist, returns a 404 response`() {
            // Arrange, Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(
                404,
                requests.updateActivity(
                    Integer.MIN_VALUE,
                    UPDATED_DESCRIPTION,
                    UPDATED_DURATION,
                    UPDATED_CALORIES,
                    updatedStarted,
                    Integer.MIN_VALUE,
                    jwtToken
                ).status,
            )
        }
    }

    @Nested
    inner class DeleteActivities {
        @Test
        fun `deleting an activity when it doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an activity that doesn't exist
            assertEquals(404, requests.deleteActivity(Integer.MIN_VALUE, jwtToken).status)
        }

        @Test
        fun `deleting a activity when it exists, returns a 204 response`() {
            // Arrange - add the activity that we plan to do a delete on
            val retrievedUserResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            val retrievedUser: User = jsonToObject(retrievedUserResponse.body.toString())
            val addActivityResponse = requests.addActivity(VALID_DESCRIPTION, VALID_DURATION, VALID_CALORIES, validStarted, retrievedUser.id, jwtToken)
            val addedActivity: Activity = jsonToObject(addActivityResponse.body.toString())

            // Act & Assert - delete the added activity and assert a 204 is returned
            assertEquals(204, requests.deleteActivity(addedActivity.id, jwtToken).status)

            // Act & Assert - attempt to retrieve the deleted activity --> 404 response
            assertEquals(404, requests.retrieveActivityById(addedActivity.id, jwtToken).status)
        }

        @Test
        fun `deleting an activity by user when user id doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete an activity that doesn't exist

            assertEquals(404, requests.deleteActivity(Integer.MIN_VALUE, jwtToken).status)
        }
    }
}
