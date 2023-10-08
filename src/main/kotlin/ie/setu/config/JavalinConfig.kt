package ie.setu.config

import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.HealthTrackerController
import ie.setu.utils.authentication.JwtProvider
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import javalinjwt.JWTAccessManager



class JavalinConfig {
    fun startJavalinService(): Javalin {

        val app = Javalin.create {
            it.accessManager(JWTAccessManager("level", rolesMapping, Roles.ANYONE))
        }.apply {
            exception(Exception::class.java)  { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getRemoteAssignedPort())

        app.before(JwtProvider.decodeHandler)

        registerRoutes(app)

        return app
    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/users") {
                get(HealthTrackerController::getAllUsers)
                post(HealthTrackerController::addUser, Roles.ANYONE)
                path("{user-id}") {
                    get(HealthTrackerController::getUserByUserId)
                    delete(HealthTrackerController::deleteUser)
                    patch(HealthTrackerController::updateUser)
                }
                path("/email/{email}") {
                    get(HealthTrackerController::getUserByEmail)
                }
            }
            path("/api/authentication") {
                path("/login") {
                    post(AuthenticationController::login, Roles.ANYONE)
                }
                path("/validate") {
                    get(AuthenticationController::validate, Roles.USER)
                }
            }
        }
    }
}