package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ActivityDAO {
    /**
     * Retrieves and returns a list of all activities in the database, regardless of user ID.
     *
     * @return A list of Activity objects representing all activities in the database.
     */
    fun getAll(): ArrayList<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }
        return activitiesList
    }

    /**
     * Finds and returns a specific activity by its activity ID.
     *
     * @param id The unique ID of the activity to find.
     * @return An Activity object representing the found activity, or null if not found.
     */
    fun findByActivityId(id: Int): Activity?{
        return transaction {
            Activities
                .select() { Activities.id eq id}
                .map{mapToActivity(it)}
                .firstOrNull()
        }
    }

    /**
     * Finds and returns all activities associated with a specific user ID.
     *
     * @param userId The ID of the user whose activities are to be retrieved.
     * @return A list of Activity objects representing the user's activities.
     */
    fun findByUserId(userId: Int): List<Activity>{
        return transaction {
            Activities
                .select {Activities.userId eq userId}
                .map {mapToActivity(it)}
        }
    }

    /**
     * Saves a new activity to the database.
     *
     * @param activity The Activity object to be saved.
     * @return The ID of the newly created activity in the database.
     */
    fun save(activity: Activity): Int {
        return transaction {
            Activities.insert {
                it[description] = activity.description
                it[duration] = activity.duration
                it[started] = activity.started
                it[calories] = activity.calories
                it[userId] = activity.userId
            } get Activities.id
        }
    }

    /**
     * Deletes an activity from the database by its activity ID.
     *
     * @param id The unique ID of the activity to be deleted.
     * @return The number of rows affected (0 or 1) indicating the success of deletion.
     */
    fun delete(id: Int): Int {
        return transaction{
            Activities.deleteWhere{
                Activities.id eq id
            }
        }
    }

    /**
     * Deletes all activities associated with a specific user ID from the database.
     *
     * @param userId The ID of the user whose activities are to be deleted.
     * @return The number of rows affected, indicating the success of deletion.
     */
    fun deleteByUserId(userId: Int): Int {
        return transaction{
            Activities.deleteWhere{
                Activities.userId eq userId
            }
        }
    }

    /**
     * Updates an existing activity in the database by its activity ID.
     *
     * @param id The unique ID of the activity to be updated.
     * @param activity The updated Activity object with new data.
     * @return The number of rows affected (0 or 1) indicating the success of the update.
     */
    fun update(id: Int, activity: Activity): Int {
        return transaction {
            Activities.update ({
                Activities.id eq id}) {
                it[description] = activity.description
                it[duration] = activity.duration
                it[started] = activity.started
                it[calories] = activity.calories
                it[userId] = activity.userId
            }
        }
    }
}