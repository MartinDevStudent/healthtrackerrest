package ie.setu.domain

import org.joda.time.DateTime

data class Activity(
    var id: Int,
    var description: String,
    var duration: Double,
    var calories: Int,
    var started: DateTime,
    var userId: Int,
)
{
    fun validate():  MutableMap<String, String> {
        val errorDetails = mutableMapOf<String, String>()

        if (this.calories < 0)  errorDetails["calories"] = "Calories cannot be less than zero"
        if (this.description.isBlank())  errorDetails["description"] = "Description cannot be blank"
        if (this.duration < 0) errorDetails["duration"] = "Duration cannot be less than zero"

        return errorDetails
    }
}
