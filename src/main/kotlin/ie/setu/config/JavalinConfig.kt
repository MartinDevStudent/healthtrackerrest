package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.IngredientController
import ie.setu.controllers.MealController
import ie.setu.controllers.UserController
import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.decodeJWT
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.crud
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.Context
import io.javalin.json.JavalinJackson
import io.javalin.vue.VueComponent
import javalinjwt.JWTAccessManager
import jsonObjectMapper

class JavalinConfig {
    val app =
        Javalin.create { config ->
            config.accessManager(JWTAccessManager("level", rolesMapping, Roles.ANYONE))
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

    /**
     * Registers API routes for various endpoints in the Javalin application.
     *
     * @param app The Javalin instance to register the routes with.
     */
    private fun registerRoutes(app: Javalin) {
        app.routes {
            // Activities
            crud("api/activities/{activity-id}", ActivityController, Roles.USER)

            // Login
            path("api/login") {
                post(AuthenticationController::login, Roles.ANYONE)
                path("validate") {
                    get(AuthenticationController::validate, Roles.USER, Roles.ADMIN)
                }
            }

            // Ingredients
            path("api/ingredients") {
                get(IngredientController::getAll, Roles.ANYONE)
                path("rda") {
                    get(IngredientController::getRecommendedDailyAllowances, Roles.ANYONE)
                }
                path("{ingredient-id}") {
                    get(IngredientController::getOne, Roles.ANYONE)
                }
            }

            // Meals
            path("api/meals") {
                get(MealController::getAll, Roles.ANYONE)
                post(MealController::create, Roles.ANYONE)
                path("{meal-id}") {
                    get(MealController::getOne, Roles.ANYONE)
                    delete(MealController::delete, Roles.ANYONE)
                    path("ingredients") {
                        get(MealController::getIngredientsByMealId, Roles.ANYONE)
                    }
                }
            }

            // User
            crud("api/users/{user-id}", UserController, Roles.ANYONE)
            path("api/users/{user-id}") {
                path("activities") {
                    get(ActivityController::getByUserId, Roles.ANYONE)
                    delete(ActivityController::deleteByUserId, Roles.ANYONE)
                }
                path("meals") {
                    get(MealController::getByUserId, Roles.ANYONE)
                    post(MealController::createUserMeal, Roles.ANYONE)
                    delete(MealController::deleteUserMealsByUserId, Roles.ANYONE)
                    path("{meal-id}") {
                        delete(MealController::deleteUserMealByMealId, Roles.ANYONE)
                    }
                }
            }
            path("api/users/email/{email-address}") {
                get(UserController::getByEmailAddress, Roles.ANYONE) // TODO: check this works
            }

            // Frontend
            get(VueComponent("<home-page></home-page>"), Roles.ANYONE)
            path("activities") {
                get(VueComponent("<activity-overview></activity-overview>"), Roles.ANYONE)
                get("{activity-id}", VueComponent("<activity-profile></activity-profile>"), Roles.ANYONE)
            }
            path("ingredients") {
                get(VueComponent("<ingredient-overview></ingredient-overview>"), Roles.ANYONE)
                get("{ingredient-id}", VueComponent("<ingredient-profile></ingredient-profile>"), Roles.ANYONE)
            }
            path("login") {
                get(VueComponent("<login-page></login-page>"), Roles.ANYONE)
            }
            path("meals") {
                get(VueComponent("<meal-overview></meal-overview>"), Roles.ANYONE)
                path("{meal-id}") {
                    get(VueComponent("<meal-profile></meal-profile>"), Roles.ANYONE)
                    get("ingredients", VueComponent("<meal-ingredient-overview></meal-ingredient-overview>"), Roles.ANYONE)
                }
            }
            path("users") {
                get(VueComponent("<user-overview></user-overview>"), Roles.ANYONE)
                path("{user-id}") {
                    get(VueComponent("<user-profile></user-profile>"), Roles.ANYONE)
                    get("activities", VueComponent("<user-activity-overview></user-activity-overview>"), Roles.ANYONE)
                }
            }
        }
    }
}
