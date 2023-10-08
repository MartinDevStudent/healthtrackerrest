package ie.setu.config

import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.HealthTrackerController
import ie.setu.controllers.MealController
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
        }.start(7000)

        app.before(JwtProvider.decodeHandler)

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
            path("/api/authentication") {
                path("/generate") {
                    get(AuthenticationController::generate, Roles.ANYONE)
                }
                path("/validate") {
                    get(AuthenticationController::validate, Roles.USER)
                }
            }
            path("/api/meals") {
                get(MealController::getAllMeals)
                post(MealController::addMeal)
                path("{meal-id}") {
                    get(MealController::getMealById)
                }
            }
        }
    }
}