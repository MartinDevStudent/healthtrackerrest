package ie.setu.domain

data class User(
    var id: Int,
    var name: String,
    var email: String,
    var level: String,
    var passwordHash: String?
)