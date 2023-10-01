package ie.setu.config

import io.javalin.security.RouteRole

enum class Roles : RouteRole {
    ANYONE,
    USER,
    ADMIN
}

var rolesMapping: HashMap<String?, RouteRole?> = object : HashMap<String?, RouteRole?>() {
    init {
        put("user", Roles.USER)
        put("admin", Roles.ADMIN)
    }
}