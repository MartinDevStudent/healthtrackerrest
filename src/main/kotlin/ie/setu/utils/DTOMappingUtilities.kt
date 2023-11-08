package ie.setu.utils

import ie.setu.domain.user.User
import ie.setu.domain.user.UserResponseDTO

object DTOMappingUtilities {
    fun userToUserResponseDto(user: User): UserResponseDTO {
        return UserResponseDTO(user.id, user.name, user.email, user.level)
    }
}
