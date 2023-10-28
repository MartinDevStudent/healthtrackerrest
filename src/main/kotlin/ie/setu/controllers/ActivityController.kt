package ie.setu.controllers

import io.javalin.http.Context
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.authentication.hashPassword
import jsonToObject
import mapObjectWithDateToJson


object ActivityController {

    private val activityDao = ActivityDAO()
    private val userDao = UserDAO()

    /**
     * Retrieves all activities from the database and sends the result as JSON response.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getAllActivities(ctx: Context) {
        val activities =  activityDao.getAll()
        if (activities.size != 0) {
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
        ctx.json(mapObjectWithDateToJson(activities))
    }

    /**
     * Retrieves an activity by its activity ID and sends it as a JSON response.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getActivityByActivityId(ctx: Context) {
        val activity = activityDao.findByActivityId(ctx.pathParam("activity-id").toInt())
        if (activity != null) {
            ctx.json(mapObjectWithDateToJson(activity))
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    /**
     * Retrieves activities by a user's ID and sends them as a JSON response.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getActivitiesByUserId(ctx: Context) {
        val activities = activityDao.findByUserId(ctx.pathParam("user-id").toInt())
        if (activities.isNotEmpty()) {
            ctx.json(mapObjectWithDateToJson(activities))
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    /**
     * Adds a new activity and sends it as a JSON response.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun addActivity(ctx: Context) {
        val activity : Activity = jsonToObject(ctx.body())
        val userId = userDao.findById(activity.userId)
        if (userId != null) {
            val activityId = activityDao.save(activity)
            activity.id = activityId
            ctx.json(mapObjectWithDateToJson(activity))
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    /**
     * Updates an activity and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun updateActivity(ctx: Context) {
        val foundActivity : Activity = jsonToObject(ctx.body())

        if ((activityDao.update(id = ctx.pathParam("activity-id").toInt(), activity=foundActivity)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    /**
     * Deletes an activity by its activity ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun deleteActivity(ctx: Context) {
        if (activityDao.delete(ctx.pathParam("activity-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    /**
     * Deletes activities associated with a user by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun deleteActivitiesByUserId(ctx: Context) {
        if (activityDao.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}