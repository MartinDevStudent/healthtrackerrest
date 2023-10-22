package ie.setu.controllers

import io.javalin.http.Context
import ie.setu.domain.User
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.authentication.hashPassword
import jsonToObject
import mapJsonToType


object UserController {

    private val userDao = UserDAO()

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

    fun addUser(ctx: Context) {
        val userDTO = mapJsonToType<UserDTO>(ctx.body())
        val passwordHash = hashPassword(userDTO.password)

        val user = User(
            id = -1,
            name = userDTO.name,
            email =  userDTO.email,
            level = "user",
            passwordHash = passwordHash,
        )

        val userId = userDao.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    fun deleteUser(ctx: Context) {
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

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