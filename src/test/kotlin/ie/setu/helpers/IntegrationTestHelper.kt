package ie.setu.helpers

import ie.setu.config.DbConfig
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime

class IntegrationTestHelper {
    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    // helper function to log in to the site and retrieve JWT token
    fun login(
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/login")
            .body("{\"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    fun retrieveActivities(): HttpResponse<String> {
        return Unirest.get("$origin/api/activities/").asString()
    }

    // helper function to retrieve a test activity from the database by activity id
    fun retrieveActivityById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/activities/$id").asString()
    }

    // helper function to retrieve a test activities from the database by user id
    fun retrieveActivitiesByUserId(userId: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$userId/activities").asString()
    }

    // helper function to add a test activity to the database
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

    // helper function to update a test activity to the database
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

    // helper function to delete a test activity from the database
    fun deleteActivity(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/activities/$id").asString()
    }

    // helper function to retrieve all users from the database
    fun retrieveUsers(): HttpResponse<String> {
        return Unirest.get("$origin/api/users/").asString()
    }

    // helper function to retrieve a test user from the database by id
    fun retrieveUserById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$id").asString()
    }

    fun retrieveUserByEmail(email: String): HttpResponse<String> {
        return Unirest.get("$origin/api/users/email/$email").asString()
    }

    // helper function to add a test user to the database
    fun addUser(
        name: String,
        email: String,
        password: String,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users")
            .body("{\"name\":\"$name\", \"email\":\"$email\", \"password\":\"$password\"}")
            .asJson()
    }

    // helper function to add a test user to the database
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

    // helper function to delete a test user from the database
    fun deleteUser(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id").asString()
    }

    fun retrieveIngredients(): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/").asString()
    }

    // helper function to retrieve a test ingredient from the database by id
    fun retrieveIngredientById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/$id").asString()
    }

    // helper function to retrieve ingredients from the database by meal id
    fun retrieveIngredientByMealId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id/ingredients").asString()
    }

    // helper function to retrieve recommended daily allowances from the database
    fun retrieveRecommendedDailyAllowances(): HttpResponse<String> {
        return Unirest.get("$origin/api/ingredients/rda").asString()
    }

    // helper function to retrieve all meals from the database
    fun retrieveMeals(): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/").asString()
    }

    // helper function to retrieve a test meal by meal id from the database
    fun retrieveMealById(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/meals/$id").asString()
    }

    // helper function to retrieve a test meal from the database by user id
    fun retrieveMealByUserId(id: Int): HttpResponse<String> {
        return Unirest.get("$origin/api/users/$id/meals").asString()
    }

    // helper function to add a test meal to the database
    fun addMeal(name: String): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    // helper function to add a test meal associated with a user to the database
    fun addMealByUserId(
        name: String,
        userId: Int,
    ): HttpResponse<JsonNode> {
        return Unirest.post("$origin/api/users/$userId/meals")
            .body("{\"name\":\"$name\"}")
            .asJson()
    }

    // helper function to delete a test meal from the database
    fun deleteMeal(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/meals/$id").asString()
    }

    // helper function to delete all meals associated with a user from the database
    fun deleteMealsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$id/meals").asString()
    }

    // helper function to delete a meal associated with a user from the database
    fun deleteUserMealByMealId(
        userId: Int,
        mealId: Int,
    ): HttpResponse<String> {
        return Unirest.delete("$origin/api/users/$userId/meals/$mealId").asString()
    }
}