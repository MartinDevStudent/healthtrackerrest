package ie.setu.config

import io.javalin.security.RouteRole
import javalinjwt.JWTAccessManager

enum class Roles : RouteRole {
    ANYONE,
    USER,
    ADMIN,
}

/**
 * Configuration for the JWTAccessManager, which manages access control based on JSON Web Tokens (JWT).
 *
 * The [JWTAccessManager] is configured with a predefined set of roles and their mappings.
 *
 * @property rolesMapping A [HashMap] that defines the mapping of role names to [RouteRole] instances.
 *         Default values include "user" mapped to [Roles.USER] and "admin" mapped to [Roles.ADMIN].
 *
 * @return An instance of [JWTAccessManager] configured with the specified parameters:
 * - `level`: The level of access control.
 * - `rolesMapping`: The mapping of role names to [RouteRole] instances.
 * - `defaultRole`: The default role assigned when no specific role is provided. Default is [Roles.ANYONE].
 *
 * Example Usage:
 * ```kotlin
 * val accessManager = accessManagerConfig
 * ```
 */
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
