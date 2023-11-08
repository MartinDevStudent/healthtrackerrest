package ie.setu.domain.user

data class UserResponseDTO(
    var id: Int,
    var name: String,
    var email: String,
    var level: String,
)
