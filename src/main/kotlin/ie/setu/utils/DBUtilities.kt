package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
import ie.setu.domain.repository.RecommendedDailyAllowancesDAO
import org.jetbrains.exposed.sql.ResultRow

/**
 * Maps a database query result row to an Activity object.
 *
 * @param it The ResultRow representing a row of activity data from the database.
 * @return An Activity object populated with data from the ResultRow.
 */
fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)

/**
 * Maps a database result row to an Ingredient object.
 *
 * This function is used to map the columns of a database result row to an Ingredient object. It
 * extracts the attributes (id, name, calories, etc.) from the result row and constructs an
 * Ingredient object with these values.
 *
 * @param it The database result row containing the ingredient information.
 * @return An Ingredient object constructed from the data in the result row.
 */
fun mapToIngredient(it: ResultRow) = Ingredient(
    id = it[Ingredients.id].value,
    name = it[Ingredients.name],
    calories = it[Ingredients.calories],
    servingSizeG = it[Ingredients.servingSizeG],
    fatTotalG = it[Ingredients.fatTotalG],
    fatSaturatedG = it[Ingredients.fatSaturatedG] ,
    proteinG = it[Ingredients.proteinG] ,
    sodiumMg = it[Ingredients.sodiumMg] ,
    potassiumMg = it[Ingredients.potassiumMg] ,
    cholesterolMg = it[Ingredients.cholesterolMg] ,
    carbohydratesTotalG = it[Ingredients.carbohydratesTotalG],
    fiberG = it[Ingredients.fiberG] ,
    sugarG = it[Ingredients.sugarG],
)

fun mapToRecommendedDailyAllowance(it: ResultRow) = RecommendedDailyAllowance(
    calories = it[RecommendedDailyAllowances.calories],
    fatTotalG = it[RecommendedDailyAllowances.fatTotalG],
    fatSaturatedG = it[RecommendedDailyAllowances.fatSaturatedG] ,
    proteinG = it[RecommendedDailyAllowances.proteinG] ,
    sodiumMg = it[RecommendedDailyAllowances.sodiumMg] ,
    potassiumMg = it[RecommendedDailyAllowances.potassiumMg] ,
    cholesterolMg = it[RecommendedDailyAllowances.cholesterolMg] ,
    carbohydratesTotalG = it[RecommendedDailyAllowances.carbohydratesTotalG],
    fiberG = it[RecommendedDailyAllowances.fiberG] ,
    sugarG = it[RecommendedDailyAllowances.sugarG],
)

/**
 * Maps a database result row to a Meal object.
 *
 * This function is used to map the columns of a database result row to a Meal object. It extracts
 * the 'id' and 'name' attributes from the result row and constructs a Meal object with these values.
 *
 * @param it The database result row containing the meal information.
 * @return A Meal object constructed from the data in the result row.
 */
fun mapToMeal(it: ResultRow) = Meal(
    id = it[Meals.id].value,
    name = it[Meals.name],
)

/**
 * Maps a database query result row to a User object.
 *
 * @param it The ResultRow representing a row of user data from the database.
 * @return A User object populated with data from the ResultRow.
 */
fun mapToUser(it: ResultRow) = User(
    id = it[Users.id].value,
    name = it[Users.name],
    email = it[Users.email],
    level = it[Users.level],
    passwordHash = it[Users.passwordHash]
)