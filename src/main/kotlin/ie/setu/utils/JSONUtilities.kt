import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kong.unirest.HttpResponse
import kong.unirest.JsonNode

/**
 * Jackson object mapper with JodaModule for date handling and custom configuration.
 */
val dateMapper = jacksonObjectMapper()
    .registerModule(JodaModule())
    .configure(SerializationFeature. WRITE_DATES_AS_TIMESTAMPS, false)

/**
 * Serializes an object to JSON with correct date serialization for DateTime.
 *
 * @param value The object to be serialized to JSON.
 * @return A JSON string representation of the input object with DateTime values properly serialized.
 */fun mapObjectWithDateToJson(value: Any): Any {
    return dateMapper.writeValueAsString(value)
}

/**
 * Deserializes a JSON string into an object of the specified type, with proper date handling.
 *
 * @param json The JSON string to be deserialized.
 * @return An object of the specified type representing the deserialized JSON data.
 * @throws com.fasterxml.jackson.core.JsonProcessingException if there is an issue with JSON parsing.
 */
inline fun <reified T: Any> jsonToObject(json: String) : T
        = jacksonObjectMapper()
    .registerModule(JodaModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .readValue<T>(json)

fun jsonObjectMapper(): ObjectMapper
        = ObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(JodaModule())
    .registerModule(KotlinModule.Builder().build())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)