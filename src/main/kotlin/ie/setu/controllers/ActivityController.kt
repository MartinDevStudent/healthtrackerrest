package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import jsonToObject
import mapObjectWithDateToJson

object ActivityController : CrudHandler {
    private val activityDao = ActivityDAO()
    private val userDao = UserDAO()

    /**
     * Retrieves all activities from the database and sends the response to the provided context.
     *
     * This function overrides a method, and it performs the following steps:
     *
     * 1. Retrieves all activities from the database using [activityDao.getAll()].
     * 2. Checks if any activities are present.
     *    - If activities are found, sets the response status to 200 (OK).
     *    - If no activities are found, sets the response status to 404 (Not Found).
     * 3. Converts the activities to a JSON representation using [mapObjectWithDateToJson()] and sends it as the response body.
     *
     * @param ctx The context object to which the response is sent.
     */
    override fun getAll(ctx: Context) {
        val activities = activityDao.getAll()

        if (activities.isEmpty())
            throw NotFoundResponse("No Activities in database")

        ctx.status(200)
        ctx.json(mapObjectWithDateToJson(activities))
    }

    /**
     * Retrieves a single activity based on the provided resource ID and sends the response to the given context.
     *
     * This function overrides a method and performs the following steps:
     *
     * 1. Retrieves an activity from the database using [activityDao.findByActivityId] with the specified resource ID.
     * 2. Checks if the activity is found.
     *    - If the activity is found, sets the response status to 200 (OK) and sends the activity as JSON in the response body.
     *    - If the activity is not found, sets the response status to 404 (Not Found).
     *
     * @param ctx         The context object to which the response is sent.
     * @param resourceId  The resource ID used to identify the specific activity.
     */
    override fun getOne(
        ctx: Context,
        resourceId: String,
    ) {
        val activity = activityDao.findByActivityId(resourceId.toInt())

        if (activity == null)
            throw NotFoundResponse("No activity with specified id found in database")

        ctx.json(mapObjectWithDateToJson(activity))
        ctx.status(200)
    }

    /**
     * Retrieves activities by a user's ID and sends them as a JSON response.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getByUserId(ctx: Context) {
        val activities = activityDao.findByUserId(ctx.pathParam("user-id").toInt())

        if (activities.isEmpty())
            throw NotFoundResponse("No activities associated with user id specified")

        ctx.json(mapObjectWithDateToJson(activities))
        ctx.status(200)
    }

    /**
     * Creates a new activity based on the JSON data in the request body and sends the response to the given context.
     *
     * This function overrides a method and performs the following steps:
     *
     * 1. Parses the JSON data from the request body into an [Activity] object using [jsonToObject()].
     * 2. Retrieves the user associated with the activity from the database using [userDao.findById(activity.userId)].
     * 3. Checks if the user is found.
     *    - If the user is found, saves the activity to the database using [activityDao.save()] and sets the response status to 201 (Created).
     *    - If the user is not found, sets the response status to 404 (Not Found).
     * 4. Sends the created activity as JSON in the response body.
     *
     * @param ctx The context object representing the incoming HTTP request and to which the response is sent.
     */
    override fun create(ctx: Context) {
        val activity: Activity = jsonToObject(ctx.body())
        val user = userDao.findById(activity.userId)

        val errorDetails = activity.validate()

        if (user === null) {
            errorDetails["userId"] = "invalid user id"
        }

        if (errorDetails.isNotEmpty())
            throw BadRequestResponse(message = "Invalid activity", errorDetails)

        val activityId = activityDao.save(activity)
        activity.id = activityId
        ctx.json(mapObjectWithDateToJson(activity))
        ctx.status(201)
    }

    /**
     * Updates an existing activity based on the provided resource ID and data in the request body.
     *
     * This function overrides a method and performs the following steps:
     *
     * 1. Converts the JSON request body to an [Activity] object using [jsonToObject].
     * 2. Updates the activity in the database using [activityDao.update], providing the resource ID and the updated activity.
     * 3. Checks if the update was successful.
     *    - If the update was successful, sets the response status to 204 (No Content).
     *    - If the resource ID is not found, sets the response status to 404 (Not Found).
     *
     * @param ctx         The context object to which the response is sent.
     * @param resourceId  The resource ID used to identify the activity to be updated.
     */
    override fun update(
        ctx: Context,
        resourceId: String,
    ) {
        val foundActivity: Activity = jsonToObject(ctx.body())
        val user = userDao.findById(foundActivity.userId)

        val errorDetails = foundActivity.validate()

        if (user === null) {
            errorDetails["userId"] = "Invalid user id"
        }

        if (errorDetails.isNotEmpty())
            throw BadRequestResponse(message = "Invalid activity", errorDetails)

        if ((activityDao.update(id = resourceId.toInt(), activity = foundActivity)) == 0)
            throw NotFoundResponse("No activity with specified id found")

        ctx.status(204)
    }

    /**
     * Deletes an existing activity based on the provided resource ID.
     *
     * This function overrides a method and performs the following steps:
     *
     * 1. Deletes the activity from the database using [activityDao.delete], providing the resource ID.
     * 2. Checks if the deletion was successful.
     *    - If the deletion was successful, sets the response status to 204 (No Content).
     *    - If the resource ID is not found, sets the response status to 404 (Not Found).
     *
     * @param ctx         The context object to which the response is sent.
     * @param resourceId  The resource ID used to identify the activity to be deleted.
     */
    override fun delete(
        ctx: Context,
        resourceId: String,
    ) {
        if (activityDao.delete(resourceId.toInt()) == 0)
            throw NotFoundResponse("No activity with specified id found")

        ctx.status(204)
    }

    /**
     * Deletes activities associated with a user by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun deleteByUserId(ctx: Context) {
        if (activityDao.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            throw NotFoundResponse("No user with specified id found")

        ctx.status(204)
    }
}