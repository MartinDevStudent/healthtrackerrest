package ie.setu.domain.repository

import ie.setu.domain.Meal
import ie.setu.domain.MealDto
import ie.setu.domain.db.Meals
import ie.setu.domain.db.Users
import ie.setu.domain.db.UsersMeals
import ie.setu.utils.mapToMeal
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.ArrayList

class MealDAO {

    /**
     * Retrieves a list of all meals stored in the system's database.
     *
     * This method queries the system's database to retrieve a list of all meals that have been stored.
     * It maps the database result rows to Meal objects and returns them as an ArrayList.
     *
     * @return An ArrayList containing all the meals stored in the system's database.
     */
    fun getAll(): ArrayList<Meal>  {
        val mealsList: ArrayList<Meal> = arrayListOf()
        transaction {
            Meals.selectAll().map {
                mealsList.add(mapToMeal(it)) }
        }
        return mealsList
    }

    /**
     * Retrieves a meal by its unique identifier (meal ID).
     *
     * This method queries the system's database to retrieve a specific meal based on its unique
     * identifier (meal ID). If a meal with the specified ID is found in the system, it is returned as a
     * Meal object. If no meal matches the provided ID, the method returns null.
     *
     * @param id The unique identifier (meal ID) of the meal to retrieve.
     * @return The Meal object if found, or null if no matching meal is found.
     */
    fun findByMealId(id: Int): Meal? {
        return transaction {
            Meals
                .select() { Meals.id eq id}
                .map{ mapToMeal(it) }
                .firstOrNull()
        }
    }

    /**
     * Retrieves a meal by its name from the database.
     *
     * @param name The name of the meal to search for.
     * @return A [Meal] object if a meal with the specified name is found, or null if not found.
     */
    fun findByMealName(name: String): Meal? {
        return transaction {
            Meals
                .select() { Meals.name eq name}
                .map{ mapToMeal(it) }
                .firstOrNull()
        }
    }

    /**
     * Retrieves a list of meals associated with a specific user ID from the database.
     *
     * @param id The user ID for which meals are to be retrieved.
     * @return An [ArrayList] of [Meal] objects associated with the specified user, or an empty list if none are found.
     */
    fun findByUserId(id: Int): ArrayList<Meal> {
        val mealsList: ArrayList<Meal> = arrayListOf()
        transaction {
            Meals.innerJoin(UsersMeals)
                .slice(Meals.columns)
                .select { UsersMeals.user eq id }
                .map { mealsList.add(mapToMeal(it)) }
        }

        return mealsList
    }

    /**
     * Saves a new meal to the system's database.
     *
     * This method is used to add a new meal to the system by inserting its name into the database.
     * It returns the unique identifier (meal ID) assigned to the newly added meal.
     *
     * @param meal The Meal object to be saved, containing the meal's name.
     * @return The unique identifier (meal ID) assigned to the newly added meal.
     */
    fun save (meal: MealDto): EntityID<Int> {
        return transaction {
            Meals.insert {
                it[name] = meal.name
            } get Meals.id
        }
    }

    /**
     * Associates a user with a specific meal and saves this association in the database.
     *
     * @param userID The ID of the user to associate with the meal.
     * @param mealId The ID of the meal to associate with the user.
     * @return The ID of the newly created UsersMeals association record.
     */
    fun saveUserMeal(userID: Int, mealId: Int): Int {
        return transaction {
            UsersMeals.insert {
                it[user] = EntityID(userID, Users)
                it[meal] = EntityID(mealId, Meals)
            } get UsersMeals.id
        }
    }

    /**
     * Deletes a meal from the system's database by its unique identifier (meal ID).
     *
     * This method is used to remove a meal from the system's database based on its unique identifier
     * (meal ID). It returns the number of records deleted, which should be 1 if a meal with the
     * specified ID was successfully deleted, or 0 if no matching meal was found.
     *
     * @param id The unique identifier (meal ID) of the meal to be deleted.
     * @return The number of records deleted (1 if successful, 0 if no matching meal is found).
     */
    fun delete(id: Int): Int {
        return transaction{
            Meals.deleteWhere{
                Meals.id eq id
            }
        }
    }
}
