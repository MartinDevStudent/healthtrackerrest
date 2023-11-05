package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.incorrectPassword
import ie.setu.helpers.validEmail
import ie.setu.helpers.validName
import ie.setu.helpers.validPassword
import jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerTest {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class ReadAuthentication {
        @Test
        fun `logging in with the incorrect details returns a 401 response`() {
            // Arrange
            // add a user to the database
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            val loginResponse = login(validName, validEmail, incorrectPassword)
            assertEquals(401, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }

        @Test
        fun `logging in with the correct details returns a 200 response`() {
            // Arrange
            // add a user to the database
            val addUserResponse = addUser(validName, validEmail, validPassword)
            val addedUser: User = jsonToObject(addUserResponse.body.toString())

            // Arrange & Act & Assert
            // send the login request and verify return code (using fixture data)
            val loginResponse = login(validName, validEmail, validPassword)
            assertEquals(200, loginResponse.status)

            // After - restore the db to previous state by deleting the added user
            val deleteUserResponse = deleteUser(addedUser.id)
            assertEquals(204, deleteUserResponse.status)
        }
    }

    // helper function to login into the site and retrieve JWT token
    private fun login(
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/authentication")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    // helper function to add a test user to the database
    private fun addUser(
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    // helper function to delete a test user from the database
    private fun deleteUser(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id").asString()
    }
}
