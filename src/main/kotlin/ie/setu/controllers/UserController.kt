package ie.setu.controllers

import io.javalin.http.Context
import ie.setu.domain.User
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.authentication.hashPassword
import jsonToObject

object UserController {

    private val userDao = UserDAO()

    /**
     * Retrieves and returns a list of all users as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
        ctx.json(users)
    }

    /**
     * Retrieves and returns a user by their unique user ID as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    /**
     * Retrieves and returns a user by their email as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else {
            ctx.status(404)
        }
    }

    /**
     * Adds a new user to the database based on the provided UserDTO and returns the added user as JSON.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun addUser(ctx: Context) {
        val userDTO: UserDTO = jsonToObject(ctx.body())
        val passwordHash = hashPassword(userDTO.password)

        val user = User(
            id = -1,
            name = userDTO.name,
            email =  userDTO.email,
            level = "user",
            passwordHash = passwordHash,
        )

        val userId = userDao.save(user)
        if (userId.value != null) {
            user.id = userId.value
            ctx.json(user)
            ctx.status(201)
        }
    }

    /**
     * Deletes a user by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun deleteUser(ctx: Context) {
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    /**
     * Updates a user's information by their user ID and sets the HTTP response status code.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun updateUser(ctx: Context) {
        val foundUserDto : UserDTO = jsonToObject(ctx.body())
        val passwordHash = hashPassword(foundUserDto.password)

        val foundUser = User(
            id = -1,
            name = foundUserDto.name,
            email =  foundUserDto.email,
            level = "user",
            passwordHash = passwordHash,
        )

        if ((userDao.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
}