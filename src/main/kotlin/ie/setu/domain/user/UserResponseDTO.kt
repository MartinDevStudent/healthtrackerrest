package ie.setu.domain.user

data class UserResponseDTO(
    var id: Int,
    var name: String,
    var email: String,
    var level: String,
) {
    companion object {
        fun fromUser(user: User): UserResponseDTO {
            return UserResponseDTO(user.id, user.name, user.email, user.level)
        }
    }
}
