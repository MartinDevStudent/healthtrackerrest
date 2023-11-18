package ie.setu.controllers

import ie.setu.domain.db.Users
import ie.setu.domain.user.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.VALID_EMAIL
import ie.setu.helpers.VALID_NAME
import ie.setu.helpers.VALID_PASSWORD
import jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * HealthTrackerControllerTestMockDB sets up a mock database for testing the HealthTrackerController.
 * It uses an in-memory H2 database to simulate database operations without the need for an actual database connection.
 *
 * This test class demonstrates how to interact with the HealthTrackerController and verifies its behavior
 * in a controlled environment where the database state can be easily managed and inspected.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthTrackerControllerTestMockDB {
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    companion object {
        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Test
    fun `getting a user by email when email exists, returns a 201 response`() {
        transaction {
            // Arrange – create a fake users table in the h2 database
            SchemaUtils.create(Users)
            // Arrange - add the user to the h2 database
            val addResponse = addUser(VALID_NAME, VALID_EMAIL, VALID_PASSWORD)
            assertEquals(201, addResponse.status)

            // Assert - retrieve the user from the fake database
            val retrieveResponse = retrieveUserByEmail(VALID_EMAIL)

            // Assert - verify the return code and the contents of the retrieved user
            assertEquals(200, retrieveResponse.status)
            val retrievedUser: User = jsonToObject(addResponse.body.toString())
            assertEquals(VALID_EMAIL, retrievedUser.email)
            assertEquals(VALID_NAME, retrievedUser.name)
        }
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

    private fun retrieveUserByEmail(email: String): HttpResponse<String> {
        return Unirest.get("$origin/api/users/email/$email").asString()
    }
}
