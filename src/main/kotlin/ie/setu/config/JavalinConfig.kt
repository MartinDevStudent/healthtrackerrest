package ie.setu.config

import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.HealthTrackerController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

class JavalinConfig {
    fun startJavalinService(): Javalin {

        val app = Javalin.create().apply {
            exception(Exception::class.java)  { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(7000)

        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/api/users") {
                get(HealthTrackerController::getAllUsers)
                post(HealthTrackerController::addUser)
                path("{user-id}") {
                    get(HealthTrackerController::getUserByUserId)
                    delete(HealthTrackerController::deleteUser)
                    patch(HealthTrackerController::updateUser)
                }
                path("/email/{email}") {
                    get(HealthTrackerController::getUserByEmail)
                }
            }
            path("api/authentication") {
                path("/generate") {
                    get(AuthenticationController::generate)
                }
                path("/validate") {
                    get(AuthenticationController::validate)
                }
            }
        }
    }
}