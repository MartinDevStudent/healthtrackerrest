package ie.setu.controllers

import ie.setu.domain.user.User
import ie.setu.helpers.INCORRECT_PASSWORD
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerTest {
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
    inner class ReadAuthentication {
        @Test
        fun `logging in with the incorrect details returns a 401 response`() {
            // Arrange - add a user to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            val loginResponse = requests.login(VALID_EMAIL, INCORRECT_PASSWORD)
            assertEquals(401, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `logging in with the correct details returns a 200 response`() {
            // Arrange
            // add a user to the database
            val addUserResponse = requests.addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            val loginResponse = requests.login(VALID_EMAIL, VALID_PASSWORD)
            assertEquals(200, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = requests.deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }
    }
}
