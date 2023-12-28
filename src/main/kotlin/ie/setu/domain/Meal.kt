package ie.setu.domain

data class Meal(
    var id: Int,
    val name: String,
) {
    fun validate(): MutableMap<String, String> {
        val errorDetails = mutableMapOf<String, String>()

        if (this.name.isBlank()) errorDetails["name"] = "name cannot be blank"

        return errorDetails
    }
}
