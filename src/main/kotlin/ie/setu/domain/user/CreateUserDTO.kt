package ie.setu.domain.user

data class CreateUserDTO(
    var name: String,
    var email: String,
    var password: String?,
)
