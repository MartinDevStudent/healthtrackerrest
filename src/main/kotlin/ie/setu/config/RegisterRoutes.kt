package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.AuthenticationController
import ie.setu.controllers.IngredientController
import ie.setu.controllers.MealController
import ie.setu.controllers.UserController
import io.javalin.apibuilder.ApiBuilder.crud
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.vue.VueComponent

/**
 * Registers routes for the Javalin web framework.
 *
 * This method defines various RESTful API endpoints and associates them with corresponding controller methods.
 * It also specifies the required roles for accessing each endpoint.
 */
fun registerRoutes(app: io.javalin.Javalin) {
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
                get(
                    "ingredients",
                    VueComponent("<meal-ingredient-overview></meal-ingredient-overview>"),
                    Roles.ANYONE,
                )
            }
        }
        path("users") {
            get(VueComponent("<user-overview></user-overview>"), Roles.ANYONE)
            path("{user-id}") {
                get(VueComponent("<user-profile></user-profile>"), Roles.ANYONE)
                get(
                    "activities",
                    VueComponent("<user-activity-overview></user-activity-overview>"),
                    Roles.ANYONE,
                )
            }
        }
    }
}
