package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.user.User
import ie.setu.helpers.INCORRECT_PASSWORD
import ie.setu.helpers.IntegrationTestHelper
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import ie.setu.utils.authentication.JwtDTO
import jsonToObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()
    private val requests = IntegrationTestHelper(origin)

    /**
     * Ensures that a user with the valid email does not exist in the system before each test.
     * If a user is found, they are deleted to maintain a clean state for tests.
     */
    @BeforeEach
    fun ensureUserDoesNotExist() {
        val loginResponse = requests.login("admin@mail.com", "password")
        val jwtDTO: JwtDTO = jsonToObject(loginResponse.body.toString())

        val token = jwtDTO.jwt

        val retrieveUserResponse = requests.retrieveUserByEmail(VALID_EMAIL, token)

        if (retrieveUserResponse.status == 200) {
            val retrievedUser: User = jsonToObject(retrieveUserResponse.body.toString())
            requests.deleteUser(retrievedUser.id, token)
        }
    }

    @Nested
    inner class ReadAuthentication {
        @Test
        fun `logging in with the incorrect details returns a 401 response`() {
            // Arrange - add a user to the database
            val addUserResponse = requests.register(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            var loginResponse = requests.login(VALID_EMAIL, INCORRECT_PASSWORD)
            assertEquals(401, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            loginResponse = requests.login(VALID_EMAIL, VALID_PASSWORD)
            val jwtDTO: JwtDTO = jsonToObject(loginResponse.body.toString())
            val deleteUserResponse = requests.deleteUser(addedUser.id, jwtDTO.jwt)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `logging in with the correct details returns a 200 response`() {
            // Arrange - add a user to the database
            val addUserResponse = requests.register(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            val loginResponse = requests.login(VALID_EMAIL, VALID_PASSWORD)
            assertEquals(200, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            val jwtDTO: JwtDTO = jsonToObject(loginResponse.body.toString())
            val deleteUserResponse = requests.deleteUser(addedUser.id, jwtDTO.jwt)
            assertEquals(204, deleteUserResponse.status)
        }
    }
}
