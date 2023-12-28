package ie.setu.controllers

import ie.setu.domain.repository.UserDAO
import ie.setu.domain.user.CreateUserDTO
import ie.setu.domain.user.User
import ie.setu.domain.user.UserResponseDTO
import ie.setu.utils.authentication.hashPassword
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import jsonToObject

/**
 * Singleton object for handling user-related operations.
 *
 * This object provides functionality for retrieving, adding, updating, and deleting user information.
 */
object UserController : CrudHandler {
    private val userDao = UserDAO()

    /**
     * Retrieves and returns a list of all users as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    override fun getAll(ctx: Context) {
        val users = userDao.getAll()

        if (users.isEmpty())
            throw NotFoundResponse("No users currently saved in database")

        ctx.status(200)
        ctx.json(
            users.map {
                UserResponseDTO.fromUser(it)
            },
        ).status()
    }

    /**
     * Retrieves and returns a user by their unique user ID as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    override fun getOne(
        ctx: Context,
        resourceId: String,
    ) {
        val user = userDao.findById(resourceId.toInt())

        if (user == null)
            throw NotFoundResponse("No users with specified id found")

        ctx.json(UserResponseDTO.fromUser(user))
        ctx.status(200)
    }

    /**
     * Retrieves and returns a user by their email as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getByEmailAddress(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email-address"))

        if (user == null)
            throw NotFoundResponse("No users with specified email address found")

        ctx.json(UserResponseDTO.fromUser(user))
        ctx.status(200)
    }

    /**
     * Adds a new user to the database based on the provided UserDTO and returns the added user as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    override fun create(ctx: Context) {
        val userDTO: CreateUserDTO = jsonToObject(ctx.body())

        val errorDetails = userDTO.validate()

        if (errorDetails.isNotEmpty())
            throw BadRequestResponse(message = "Invalid user details", errorDetails)

        val passwordHash = hashPassword(userDTO.password!!)

        val user =
            User(
                id = -1,
                name = userDTO.name,
                email = userDTO.email,
                level = "user",
                passwordHash = passwordHash,
            )

        val userId = userDao.save(user)
        user.id = userId

        ctx.json(UserResponseDTO.fromUser(user))
        ctx.status(201)
    }

    /**
     * Deletes a user by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    override fun delete(
        ctx: Context,
        resourceId: String,
    ) {
        if (userDao.delete(resourceId.toInt()) == 0)
            throw NotFoundResponse("No user with specified id found")

        ctx.status(204)
    }

    /**
     * Updates a user's information by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    override fun update(
        ctx: Context,
        resourceId: String,
    ) {
        val userId = resourceId.toInt()
        val userDto: CreateUserDTO = jsonToObject(ctx.body())
        val user = userDao.findById(userId)

        val errorDetails = userDto.validate()

        if (user === null) {
            errorDetails["userId"] = "invalid user id"
        }

        if (errorDetails.isNotEmpty())
            throw BadRequestResponse(message = "Invalid user details", errorDetails)

        user!!.name = userDto.name
        user.email = userDto.email
        user.passwordHash = if (userDto.password === null) user.passwordHash else hashPassword(userDto.password!!)

        if ((userDao.update(id = userId, user = user)) == 0)
            throw NotFoundResponse("No user with specified id found")

        ctx.json(UserResponseDTO.fromUser(user))
        ctx.status(204)
    }
}
