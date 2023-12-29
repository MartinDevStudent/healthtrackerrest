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

    /**
     * Formats a string to be used as a query parameter by replacing spaces with "%20".
     *
     * This function takes a string as input and replaces spaces in the string with "%20",
     * making it suitable for use as a query parameter in URLs.
     *
     * @param string The input string to be formatted.
     *
     * @return The formatted string with spaces replaced by "%20".
     *
     * Example Usage:
     * ```kotlin
     * val formattedQuery = formatQueryString("example query string")
     * ```
     */
    private fun formatQueryString(string: String): String {
        return string.replace(" ", "%20")
    }

    /**
     * Creates an HTTP GET request for the "https://api.api-ninjas.com/v1/nutrition" endpoint with the specified query string.
     *
     * This function constructs an [HttpRequest] object configured for a GET request to the nutrition API endpoint.
     * It includes the provided query string in the request URI and sets the required headers, such as "X-Api-Key".
     *
     * @param queryString The query string to be included in the request URI.
     *
     * @return An [HttpRequest] object configured for a GET request to the nutrition API.
     *
     * Example Usage:
     * ```kotlin
     * val query = "healthy food"
     * val httpRequest = getHttpRequest(query)
     * ```
     *
     * @see HttpRequest
     * @see URI
     */
    private fun getHttpRequest(queryString: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI("https://api.api-ninjas.com/v1/nutrition?query=$queryString"))
            .header("X-Api-Key", "Ye2v3N4QgCjtVmqLha+19Q==AQlXfNXQg9vyPp5f")
            .GET()
            .build()
    }

    /**
     * Maps an [IngredientApiDTO] to an [Ingredient] object.
     *
     * This function converts data from an API Data Transfer Object (DTO) representing an ingredient
     * to an application-specific [Ingredient] object with corresponding properties.
     *
     * @param dto The API Data Transfer Object (DTO) representing an ingredient.
     *
     * @return An [Ingredient] object with properties mapped from the provided DTO.
     *
     * Example Usage:
     * ```kotlin
     * val ingredientDto = IngredientApiDTO(/* DTO initialization */)
     * val ingredient = mapIngredientApiDtoToIngredient(ingredientDto)
     * ```
     *
     * @see IngredientApiDTO
     * @see Ingredient
     */
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
