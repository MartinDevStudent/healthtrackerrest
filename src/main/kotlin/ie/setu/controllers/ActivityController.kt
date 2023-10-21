package ie.setu.controllers

import io.javalin.http.Context
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import mapJsonWithDateToType
import mapObjectWithDateToJson


object ActivityController {

    private val activityDao = ActivityDAO()
    private val userDao = UserDAO()

    fun getAllActivities(ctx: Context) {
        ctx.json(mapObjectWithDateToJson(activityDao.getAll()))
    }

    fun getActivityActivityId(ctx: Context) {
        val activity = activityDao.findByActivityId(ctx.pathParam("activity-id").toInt())
        if (activity != null) {
            ctx.json(mapObjectWithDateToJson(activity))
        }
    }

    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDao.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                ctx.json(mapObjectWithDateToJson(activities))
            }
        }
    }

    fun addActivity(ctx: Context) {
        val activity = mapJsonWithDateToType<Activity>(ctx.body())
        activityDao.save(activity)
        ctx.json(mapObjectWithDateToJson(activity))
    }

    fun deleteActivity(ctx: Context) {
        activityDao.delete(ctx.pathParam("activity-id").toInt())
    }

    fun deleteActivitiesByUserId(ctx: Context) {
        activityDao.deleteByUserId(ctx.pathParam("user-id").toInt())
    }
}