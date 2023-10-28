package ie.setu.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.FoodItem
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

object NutrientHttpClient {
    private val mapper = jacksonObjectMapper()

    /**
     * Retrieves food items based on a meal name by making an HTTP GET request to an external API.
     *
     * @param mealName The name of the meal to query for food items.
     * @return An array of FoodItem objects representing food items related to the specified meal name.
     */
    fun get(mealName: String): Array<FoodItem> {
        // Format the query string by replacing spaces with "%20"
        val formattedQueryString = mealName.replace(" ", "%20")

        // Create an HTTP client
        val client = HttpClient.newHttpClient()

        // Build an HTTP GET request with the formatted query string and API key header
        val request = HttpRequest.newBuilder()
            .uri(URI("https://api.api-ninjas.com/v1/nutrition?query=$formattedQueryString"))
            .header("X-Api-Key", "Ye2v3N4QgCjtVmqLha+19Q==AQlXfNXQg9vyPp5f")
            .GET()
            .build()

        // Send the HTTP request and get the response as a string
        val response = client.send(request, BodyHandlers.ofString())

        // Deserialize the response body into an array of FoodItem objects
        val meals = mapper.readValue<Array<FoodItem>>(response.body())

        return meals
    }
}
