package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.Meal
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Meals
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ResultRow

/**
 * Maps a database query result row to a User object.
 *
 * @param it The ResultRow representing a row of user data from the database.
 * @return A User object populated with data from the ResultRow.
 */
fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    level = it[Users.level],
    passwordHash = it[Users.passwordHash]
)

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
 * Maps a database result row to a Meal object.
 *
 * This function is used to map the columns of a database result row to a Meal object. It extracts
 * the 'id' and 'name' attributes from the result row and constructs a Meal object with these values.
 *
 * @param it The database result row containing the meal information.
 * @return A Meal object constructed from the data in the result row.
 */
fun mapToMeal(it: ResultRow) = Meal(
    id = it[Meals.id],
    name = it[Meals.name],
)
