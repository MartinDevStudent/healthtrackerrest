package ie.setu.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Ingredient
import ie.setu.domain.IngredientApiDTO
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
    fun get(mealName: String): List<Ingredient> {
        // Format the query string by replacing spaces with "%20"
        val formattedQueryString = formatQueryString(mealName)

        // Create an HTTP client
        val client = HttpClient.newHttpClient()

        // Build an HTTP GET request with the formatted query string and API key header
        val request = getHttpRequest(formattedQueryString)

        // Send the HTTP request and get the response as a string
        val response = client.send(request, BodyHandlers.ofString())

        // Deserialize the response body into an array of IngredientApiDTO objects
        val meals = mapper.readValue<Array<IngredientApiDTO>>(response.body())

        return meals.map { mapIngredientApiDtoToIngredient(it) }
    }

    private fun formatQueryString(string: String): String {
        return string.replace(" ", "%20")
    }

    private fun getHttpRequest(queryString: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI("https://api.api-ninjas.com/v1/nutrition?query=$queryString"))
            .header("X-Api-Key", "Ye2v3N4QgCjtVmqLha+19Q==AQlXfNXQg9vyPp5f")
            .GET()
            .build()
    }

    private fun mapIngredientApiDtoToIngredient(dto: IngredientApiDTO): Ingredient {
        return Ingredient(
            id = -1,
            name = dto.name,
            calories = dto.calories,
            servingSizeG = dto.servingSizeG,
            fatTotalG = dto.fatTotalG,
            fatSaturatedG = dto.fatSaturatedG,
            proteinG = dto.proteinG,
            sodiumMg = dto.sodiumMg,
            potassiumMg = dto.potassiumMg,
            cholesterolMg = dto.cholesterolMg,
            carbohydratesTotalG = dto.carbohydratesTotalG,
            fiberG = dto.fiberG,
            sugarG = dto.sugarG,
        )
    }
}
