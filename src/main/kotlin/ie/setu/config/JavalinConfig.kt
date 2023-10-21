package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.UserController
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
            path("/api/activities") {
                get(ActivityController::getAllActivities, Roles.ANYONE)
                post(ActivityController::addActivity, Roles.ANYONE)
                path("{activity-id}") {
                    get(ActivityController::getActivityActivityId, Roles.ANYONE) // TODO
                    delete(ActivityController::deleteActivity, Roles.ANYONE) // TODO
                }
            }
            path("/api/authentication") {
                post(AuthenticationController::login, Roles.ANYONE)
                path("/validate") {
                    get(AuthenticationController::validate, Roles.USER)
                }
            }
            path("/api/meals") {
                get(MealController::getAllMeals, Roles.ANYONE)
                post(MealController::addMeal, Roles.ANYONE)
                path("{meal-id}") {
                    get(MealController::getMealById, Roles.ANYONE)
                }
            }
            path("/api/users") {
                get(UserController::getAllUsers, Roles.ANYONE)
                post(UserController::addUser, Roles.ANYONE)
                path("{user-id}") {
                    get(UserController::getUserByUserId, Roles.ANYONE)
                    delete(UserController::deleteUser, Roles.ANYONE)
                    patch(UserController::updateUser, Roles.ANYONE)
                    path("activities"){
                        get(ActivityController::getActivitiesByUserId, Roles.ANYONE)
                        delete(ActivityController::deleteActivitiesByUserId, Roles.ANYONE) // TODO
                    }
                }
                path("/email/{email}") {
                    get(UserController::getUserByEmail, Roles.ANYONE)
                }
            }
        }
    }
}