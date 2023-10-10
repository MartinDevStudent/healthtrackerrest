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

    fun get(mealName: String): Array<FoodItem> {
        val formattedQueryString = mealName.replace(" ", "%20")
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI("https://api.api-ninjas.com/v1/nutrition?query=$formattedQueryString"))
            .header("X-Api-Key", "Ye2v3N4QgCjtVmqLha+19Q==AQlXfNXQg9vyPp5f")
            .GET()
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        val meals = mapper.readValue<Array<FoodItem>>(response.body())

        return meals
    }
}
