package ie.setu.config

import ie.setu.utils.authentication.decodeJWT
import io.javalin.http.Context
import io.javalin.vue.JavalinVueConfig

/**
 * Configures the Javalin-Vue integration settings.
 *
 * This extension function sets specific configurations for integrating Javalin with Vue.js.
 *
 * @receiver The instance of [JavalinVueConfig] to be configured.
 *
 * @see JavalinVueConfig
 *
 * @property vueAppName The name of the Vue.js application. Defaults to "app".
 *
 * @property stateFunction A function that defines the initial state for the Vue.js application.
 * The function takes a [Context] parameter and returns a map representing the initial state.
 * Defaults to a map with a "user" key mapped to the result of [currentUser].
 *
 * Example Usage:
 * ```kotlin
 * val javalinVueConfig = JavalinVueConfig().configure()
 * ```
 *
 * @see currentUser
 */
fun JavalinVueConfig.configure() {
    this.vueAppName = "app"
    this.stateFunction = { ctx -> mapOf("user" to currentUser(ctx)) }
}

/**
 * Retrieves the username of the current authenticated user from the provided Javalin [Context].
 *
 * This function checks if the request in the given context is authenticated using basic authentication.
 * If authentication is successful, it decodes the JWT (JSON Web Token) and extracts the "name" claim,
 * representing the username of the authenticated user.
 *
 * @param ctx The Javalin [Context] containing information about the current HTTP request.
 *
 * @return The username of the authenticated user if available; otherwise, returns null.
 *
 * Example Usage:
 * ```kotlin
 * val username = currentUser(context)
 * ```
 *
 * @see Context
 * @see decodeJWT
 */
private fun currentUser(ctx: Context): String? {
    return if (ctx.basicAuthCredentials() != null) decodeJWT(ctx).getClaim("name").asString() else null
}
