import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val dateMapper = jacksonObjectMapper()
    .registerModule(JodaModule())
    .configure(SerializationFeature. WRITE_DATES_AS_TIMESTAMPS, false)

// Deserializes Json with DateTime to Kotlin Type
inline fun <reified T> mapJsonToType(content: String): T {
    val mapper = jacksonObjectMapper()
    return mapper.readValue<T>(content)
}

// Maps object to Json with correct serialization for DateTime
fun mapObjectWithDateToJson(value: Any): Any {
    return dateMapper.writeValueAsString(value)
}

inline fun <reified T: Any> jsonToObject(json: String) : T
        = jacksonObjectMapper()
    .registerModule(JodaModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .readValue<T>(json)