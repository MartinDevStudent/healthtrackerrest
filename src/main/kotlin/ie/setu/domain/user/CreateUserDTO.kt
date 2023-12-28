package ie.setu.domain.user

import java.util.regex.Pattern

data class CreateUserDTO(
    var name: String,
    var email: String,
    var password: String?,
) {
    fun validate(): MutableMap<String, String> {
        val errorDetails = mutableMapOf<String, String>()

        if (this.name.isBlank()) errorDetails["name"] = "name cannot be an empty string"
        if (!isValidEmail(this.email)) errorDetails["email"] = "invalid email address"
        if (!this.password.isNullOrEmpty() && this.password!!.length < 3) {
            errorDetails["password"] = "password cannot be less than three characters"
        }

        return errorDetails
    }

    private fun isValidEmail(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
                "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
                "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
                "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
                "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$",
        ).matcher(email).matches()
    }
}
