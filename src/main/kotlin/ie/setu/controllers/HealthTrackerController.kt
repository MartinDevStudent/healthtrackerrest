package ie.setu.controllers

import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.authentication.hashPassword


object HealthTrackerController {

    private val userDao = UserDAO()
    private val mapper = jacksonObjectMapper()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDao.getAll())
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
        }
    }

    fun addUser(ctx: Context) {
        val userDTO = mapper.readValue<UserDTO>(ctx.body())
        val passwordHash = hashPassword(userDTO.password)

        val user = User(
            id = -1,
            name = userDTO.name,
            email =  userDTO.email,
            level = "user",
            passwordHash = passwordHash,
        )

        userDao.save(user)
    }

    fun deleteUser(ctx: Context) {
        userDao.delete(ctx.pathParam("user-id").toInt())
    }

    fun updateUser(ctx: Context) {
        val userToUpdate = mapper.readValue<User>(ctx.body())
        userDao.update(
            id = ctx.pathParam("user-id").toInt(),
            user = userToUpdate
        )
        ctx.json(userToUpdate)
    }
}