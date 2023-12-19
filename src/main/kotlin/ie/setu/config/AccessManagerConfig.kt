package ie.setu.config

import io.javalin.security.RouteRole
import javalinjwt.JWTAccessManager

enum class Roles : RouteRole {
    ANYONE,
    USER,
    ADMIN,
}

val accessManagerConfig: JWTAccessManager
    get() {
        val rolesMapping: HashMap<String?, RouteRole?> =
            object : HashMap<String?, RouteRole?>() {
                init {
                    put("user", Roles.USER)
                    put("admin", Roles.ADMIN)
                }
            }

        return JWTAccessManager("level", rolesMapping, Roles.ANYONE)
    }
