package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.UserDTO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.authentication.*
import io.javalin.http.Context


object AuthenticationController {
    private val userDao = UserDAO()

    /**
     * Handles user login and issues a JSON Web Token (JWT) upon successful authentication.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun login(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val userDTO = mapper.readValue<UserDTO>(ctx.body())

        val user = userDao.findByEmail(userDTO.email)

        if (user == null) {
                ctx.status(401)
        }

        val isCorrectPassword = isCorrectPassword(userDTO.password, user!!.passwordHash!!)

        if (isCorrectPassword) {
            val token = JwtProvider.provider.generateToken(user)
            ctx.json(JwtResponse(token))
            ctx.status(200)
        }
        else {
            ctx.status(401)
        }
    }

    /**
     * Validates a user's JWT and returns a greeting message.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun validate(ctx: Context) {
        try {
            val decodedJWT = decodeJWT(ctx)
            ctx.result("Hi " + decodedJWT.getClaim("name").asString())
            ctx.status(200)
        }
        catch (e: Exception) {
            ctx.status(401)
        }
    }
}