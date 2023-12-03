package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.user.User
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.NONE_EXISTING_EMAIL
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.UPDATED_EMAIL
import ie.setu.helpers.UPDATED_NAME
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    private val requests = IntegrationTestHelper(origin)

    /**
     * Ensures that users with the specified emails do not exist in the system.
     * If a user with either VALID_EMAIL or UPDATED_EMAIL exists, they are deleted.
     * This is a setup method that runs before each test to maintain a clean state.
     */
    @BeforeEach
    fun ensureUserDoesNotExist() {
        val responseOne = requests.retrieveUserByEmail(VALID_EMAIL)
        val responseTwo = requests.retrieveUserByEmail(UPDATED_EMAIL)

        if (responseOne.status == 200) {
            val retrievedUserOne: User = jsonToObject(responseOne.body.toString())
            requests.deleteUser(retrievedUserOne.id)
        }

        if (responseTwo.status == 200) {
            val retrievedUserTwo: User = jsonToObject(responseTwo.body.toString())
            requests.deleteUser(retrievedUserTwo.id)
        }
    }

    @Nested
    inner class ReadUsers {
        @Test
        fun `get all users from the database returns 200 or 404 response`() {
            val response = requests.retrieveUsers()
            if (response.status == 200) {
                val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                assertNotEquals(0, retrievedUsers.size)
            } else {
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get user by id when user does not exist returns 404 response`() {
            // Arrange - test data for user id
            val id = Integer.MIN_VALUE

            // Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = requests.retrieveUserById(id)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `get user by email when user does not exist returns 404 response`() {
            // Arrange & Act - attempt to retrieve the non-existent user from the database
            val retrieveResponse = requests.retrieveUserByEmail(NONE_EXISTING_EMAIL)

            // Assert -  verify return code
            assertEquals(404, retrieveResponse.status)
        }

        @Test
        fun `getting a user by id when id exists, returns a 200 response`() {
            // Arrange - add the user
            val addResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addResponse.body.toString())

            // Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = requests.retrieveUserById(addedUser.id)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `getting a user by email when email exists, returns a 200 response`() {
            // Arrange - add the user
            requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)

            // Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            assertEquals(200, retrieveResponse.status)

            // After - restore the db to previous state by deleting the added user
            val retrievedUser: User = jsonToObject(retrieveResponse.body.toString())
            val deleteUserResponse = requests.deleteUser(retrievedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }
    }

    @Nested
    inner class CreateUsers {
        @Test
        fun `add a user with correct details returns a 201 response`() {
            // Arrange & Act & Assert
            //    add the user and verify return code (using fixture data)
            val addResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            assertEquals(201, addResponse.status)

            // Assert - retrieve the added user from the database and verify return code
            val retrieveResponse = requests.retrieveUserByEmail(VALID_EMAIL)
            assertEquals(200, retrieveResponse.status)

            // Assert - verify the contents of the retrieved user
            val retrievedUser: User = jsonToObject(addResponse.body.toString())
            assertEquals(VALID_EMAIL, retrievedUser.email)
            assertEquals(VALID_NAME, retrievedUser.name)
            assertEquals("user", retrievedUser.level)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(retrievedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }
    }

    @Nested
    inner class UpdateUsers {
        @Test
        fun `updating a user when it exists, returns a 204 response`() {
            // Arrange - add the user that we plan to do an update on and ensure user with update email does not exist
            val addedResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addedResponse.body.toString())

            // Act & Assert - update the email and name of the retrieved user and assert 204 is returned
            assertEquals(204, requests.updateUser(addedUser.id, UPDATED_NAME, UPDATED_EMAIL, VALID_PASSWORD).status)

            // Act & Assert - retrieve updated user and assert details are correct
            val updatedUserResponse = requests.retrieveUserById(addedUser.id)
            val updatedUser: User = jsonToObject(updatedUserResponse.body.toString())
            assertEquals(UPDATED_NAME, updatedUser.name)
            assertEquals(UPDATED_EMAIL, updatedUser.email)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `updating a user when it doesn't exist, returns a 404 response`() {
            // Arrange, Act & Assert - attempt to update the email and name of user that doesn't exist
            assertEquals(404, requests.updateUser(Integer.MIN_VALUE, UPDATED_NAME, UPDATED_EMAIL, VALID_PASSWORD).status)
        }
    }

    @Nested
    inner class DeleteUsers {
        @Test
        fun `deleting a user when it doesn't exist, returns a 404 response`() {
            // Act & Assert - attempt to delete a user that doesn't exist
            assertEquals(404, requests.deleteUser(Integer.MIN_VALUE).status)
        }

        @Test
        fun `deleting a user when it exists, returns a 204 response`() {
            // Arrange - add the user that we plan to do a delete on
            val addedResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addedResponse.body.toString())

            // Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, requests.deleteUser(addedUser.id).status)

            // Act & Assert - attempt to retrieve the deleted user --> 404 response
            assertEquals(404, requests.retrieveUserById(addedUser.id).status)
        }
    }
}
