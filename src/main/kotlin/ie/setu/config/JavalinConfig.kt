package ie.setu.config

import ie.setu.utils.authentication.JwtProvider
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import jsonObjectMapper

class JavalinConfig {
    val app =
        Javalin.create { config ->
            config.accessManager(accessManagerConfig)
            // Added this jsonMapper for our integration tests - serialise objects to json
            config.jsonMapper(JavalinJackson(jsonObjectMapper()))
            config.staticFiles.enableWebjars()
            config.vue.configure()
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 : Not Found") }
        }

    /**
     * Starts the Javalin web service, initializes necessary configurations, and registers routes.
     *
     * This function is responsible for starting the Javalin web service, including setting up the assigned port,
     * decoding JWTs in the request headers, and registering routes for handling incoming requests.
     *
     * @return The configured and started [Javalin] instance representing the running web service.
     *
     * Example Usage:
     * ```kotlin
     * val javalinInstance = startJavalinService()
     * ```
     *
     * @see Javalin
     * @see JwtProvider.decodeHandler
     * @see registerRoutes
     * @see getRemoteAssignedPort
     */
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
}
