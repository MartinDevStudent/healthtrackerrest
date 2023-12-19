package ie.setu.config

import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.decodeJWT
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.json.JavalinJackson
import jsonObjectMapper

class JavalinConfig {
    val app =
        Javalin.create { config ->
            config.accessManager(AccessManagerConfig())
            // Added this jsonMapper for our integration tests - serialise objects to json
            config.jsonMapper(JavalinJackson(jsonObjectMapper()))
            config.staticFiles.enableWebjars()
            config.vue.vueAppName = "app" // only required for Vue 3, is defined in layout.html
            config.vue.stateFunction = { ctx -> mapOf("user" to currentUser(ctx)) }
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 : Not Found") }
        }

    fun startJavalinService(): Javalin {
        app.start(getRemoteAssignedPort())
        app.before(JwtProvider.decodeHandler)
        registerRoutes(app)
        return app
    }

    /**
     * Retrieves the current instance of the Javalin app.
     *
     * This function is used to obtain the configured Javalin app instance for further operations such as adding routes, starting the server, etc.
     *
     * @return The configured Javalin app instance.
     */
    fun getJavalinService(): Javalin {
        app.before(JwtProvider.decodeHandler)
        registerRoutes(app)
        return app
    }

    /**
     * Gets the assigned port for the Javalin service, falling back to a default port if not set.
     *
     * @return The port number to use.
     */
    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else {
            7000
        }
    }

    private fun currentUser(ctx: Context): String? {
        return if (ctx.basicAuthCredentials() != null) decodeJWT(ctx).getClaim("name").asString() else null
    }
}
