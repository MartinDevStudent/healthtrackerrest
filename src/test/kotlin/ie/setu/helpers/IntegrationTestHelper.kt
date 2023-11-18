package ie.setu.helpers

import ie.setu.config.DbConfig
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime

/**
 * IntegrationTestHelper provides utility functions for integration testing,
 * It sets up a connection to the database and starts the server for testing purposes
 * and provides a collection functions for sending http request to the server
 */
class IntegrationTestHelper {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    /**
     * Performs a user login by sending a POST request to the specified API endpoint.
     *
     * This method sends a POST request to the login API endpoint with the provided email and password.
     * The request body is formatted as JSON with the email and password parameters. The response from
     * the server is expected to be in JSON format. The method returns an `HttpResponse<JsonNode>` object
     * representing the response from the server.
     *
     * @param email The user's email address for authentication.
     * @param password The user's password for authentication.
     * @return An `HttpResponse<JsonNode>` object containing the server's response in JSON format.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun login(
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/login")
            .body("{\"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    /**
     * Retrieves a list of all activities from the server.
     *
     * @return HttpResponse<String> containing the server's response as a string.
     */
    fun retrieveActivities(): HttpResponse<String> {
        return Unirest.get("$origin/api/activities/").asString()
    }

    /**
     * Retrieves a list of activities associated with a specific user.
     *
     * @param userId The unique identifier of the user whose activities are to be retrieved.
     * @return An HttpResponse containing the string representation of the activities.
     */
    fun retrieveActivityById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/activities/$id").asString()
    }

    /**
     * Retrieves activities for a user based on their user ID by sending a GET request to the specified API endpoint.
     *
     * This method sends a GET request to the activities API endpoint for a specific user identified by the provided
     * user ID. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param userId The unique identifier of the user for whom activities are to be retrieved.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun retrieveActivitiesByUserId(userId: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$userId/activities").asString()
    }

    /**
     * Adds a new activity to the database for a specified user.
     *
     * @param description A description of the activity.
     * @param duration The duration of the activity in minutes.
     * @param calories The number of calories burned during the activity.
     * @param started The date and time when the activity started.
     * @param userId The ID of the user who performed the activity.
     * @return An HttpResponse containing the JSON response from the server.
     */
    fun addActivity(
        description: String,
        duration: Double,
        calories: Int,
        started: DateTime,
        userId: Int,
    ): HttpResponse<JsonNode> {
        @Suppress("ktlint:standard:max-line-length")
        return Unirest.post("$origin/api/activities")
            .body(
                "{\"description\":\"$description\", \"duration\":\"$duration\", \"calories\":\"$calories\", \"started\":\"$started\", \"userId\":\"$userId\"}",
            )
            .asJson()
    }

    /**
     * Updates an existing activity in the database with the provided details.
     *
     * @param id The unique identifier of the activity to be updated.
     * @param description A brief description of the activity.
     * @param duration The duration of the activity in minutes.
     * @param calories The number of calories burned during the activity.
     * @param started The date and time when the activity started.
     * @param userId The unique identifier of the user who performed the activity.
     * @return An HttpResponse containing the JSON response from the server.
     */
    fun updateActivity(
        id: Int,
        description: String,
        duration: Double,
        calories: Int,
        started: DateTime,
        userId: Int,
    ): HttpResponse<JsonNode> {
        @Suppress("ktlint:standard:max-line-length")
        return Unirest.patch("$origin/api/activities/$id")
            .body(
                "{\"id\":\"$id\", \"description\":\"$description\", \"duration\":\"$duration\", \"calories\":\"$calories\", \"started\":\"$started\", \"userId\":\"$userId\"}",
            )
            .asJson()
    }

    /**
     * Deletes an activity with the given ID from the database.
     *
     * @param id The ID of the activity to be deleted.
     * @return The HTTP response indicating the result of the delete operation.
     */
    fun deleteActivity(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/activities/$id").asString()
    }

    /**
     * Retrieves all users from the database.
     *
     * @return HttpResponse<String> containing the response data as a string.
     */
    fun retrieveUsers(): HttpResponse<String> {
        return Unirest.get("$origin/api/users/").asString()
    }

    /**
     * Retrieves user information based on the user's unique identifier (ID) by sending a GET request to the specified API endpoint.
     *
     * This method sends a GET request to the user API endpoint for a specific user identified by the provided
     * user ID. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param id The unique identifier of the user for whom information is to be retrieved.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun retrieveUserById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$id").asString()
    }

    /**
     * Retrieves user information based on the user's email address by sending a GET request to the specified API endpoint.
     *
     * This method sends a GET request to the user API endpoint for a specific user identified by the provided
     * email address. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param email The email address of the user for whom information is to be retrieved.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun retrieveUserByEmail(email: String): HttpResponse<String> {
        return Unirest.get("$origin/api/users/email/$email").asString()
    }

    /**
     * Adds a new user to the database with the given name, email, and password.
     *
     * @param name The name of the user to be added.
     * @param email The email of the user to be added.
     * @param password The password for the user to be added.
     * @return An HttpResponse containing the JSON response from the server.
     */
    fun addUser(
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    /**
     * Updates user information by sending a PATCH request to the specified API endpoint.
     *
     * This method sends a PATCH request to the user API endpoint for a specific user identified by the provided
     * user ID. The request body is formatted as JSON with the updated user information, including the new name,
     * email, and password. The response from the server is expected to be in JSON format. The method returns
     * an `HttpResponse<JsonNode>` object containing the server's response in JSON format.
     *
     * @param id The unique identifier of the user to be updated.
     * @param name The new name for the user.
     * @param email The new email address for the user.
     * @param password The new password for the user.
     * @return An `HttpResponse<JsonNode>` object containing the server's response in JSON format.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun updateUser(
        id: Int,
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.patch("$origin/api/users/$id")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param id The unique identifier of the user to be deleted.
     * @return HttpResponse<String> The response from the server after attempting to delete the user.
     */
    fun deleteUser(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id").asString()
    }

    /**
     * Retrieves a list of all ingredients from the server.
     *
     * @return An HttpResponse containing the JSON response with the list of ingredients.
     */
    fun retrieveIngredients(): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/").asString()
    }

    /**
     * Retrieves information about an ingredient based on its unique identifier (ID) by sending a GET request to the specified API endpoint.
     *
     * This method sends a GET request to the ingredient API endpoint for a specific ingredient identified by the provided
     * ingredient ID. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param id The unique identifier of the ingredient for which information is to be retrieved.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun retrieveIngredientById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/$id").asString()
    }

    /**
     * Retrieves a list of ingredients associated with a specific meal by the meal's ID.
     *
     * @param id The unique identifier of the meal.
     * @return An HttpResponse containing the ingredients as a JSON string.
     */
    fun retrieveIngredientByMealId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id/ingredients").asString()
    }

    /**
     * Retrieves information about recommended daily allowances (RDAs) for ingredients by sending a GET request to the specified API endpoint.
     *
     * This method sends a GET request to the recommended daily allowances (RDA) API endpoint for ingredients.
     * The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun retrieveRecommendedDailyAllowances(): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/rda").asString()
    }

    /**
     * Retrieves a list of all meals from the database.
     *
     * @return HttpResponse containing the response string from the server.
     */
    fun retrieveMeals(): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/").asString()
    }

    /**
     * Retrieves a meal by its unique identifier.
     *
     * @param id The unique identifier of the meal to be retrieved.
     * @return An HttpResponse containing the meal data as a string.
     */
    fun retrieveMealById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id").asString()
    }

    /**
     * Retrieves a list of meals associated with a given user ID.
     *
     * @param id The unique identifier of the user.
     * @return An HttpResponse containing the JSON string of the retrieved meals.
     */
    fun retrieveMealByUserId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$id/meals").asString()
    }

    /**
     * Adds a new meal to the database with the given name.
     *
     * @param name The name of the meal to be added.
     * @return An HttpResponse containing the JsonNode of the created meal.
     */
    fun addMeal(name: String): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    /**
     * Adds a new meal for a user by sending a POST request to the specified API endpoint.
     *
     * This method sends a POST request to the meals API endpoint for a specific user identified by the provided
     * user ID. The request body is formatted as JSON with the new meal's name. The response from the server is
     * expected to be in JSON format. The method returns an `HttpResponse<JsonNode>` object containing the server's
     * response in JSON format.
     *
     * @param name The name of the new meal to be added.
     * @param userId The unique identifier of the user for whom the meal is to be added.
     * @return An `HttpResponse<JsonNode>` object containing the server's response in JSON format.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun addMealByUserId(
        name: String,
        userId: Int,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users/$userId/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    /**
     * Deletes a meal from the database by its unique ID.
     *
     * @param id The unique identifier of the meal to be deleted.
     * @return An HttpResponse containing the result of the deletion request.
     */
    fun deleteMeal(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/meals/$id").asString()
    }

    /**
     * Deletes all meals for a user by sending a DELETE request to the specified API endpoint.
     *
     * This method sends a DELETE request to the meals API endpoint for a specific user identified by the provided
     * user ID. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param id The unique identifier of the user for whom all meals are to be deleted.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun deleteMealsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id/meals").asString()
    }

    /**
     * Deletes a specific meal for a user by sending a DELETE request to the specified API endpoint.
     *
     * This method sends a DELETE request to the meals API endpoint for a specific user and meal identified by the provided
     * user ID and meal ID. The response from the server is expected to be a string. The method returns an `HttpResponse<String>`
     * object containing the server's response as a string.
     *
     * @param userId The unique identifier of the user to whom the meal belongs.
     * @param mealId The unique identifier of the meal to be deleted.
     * @return An `HttpResponse<String>` object containing the server's response as a string.
     * @throws UnirestException if an error occurs during the HTTP request.
     */
    fun deleteUserMealByMealId(
        userId: Int,
        mealId: Int,
    ): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$userId/meals/$mealId").asString()
    }
}