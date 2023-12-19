package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.user.UserLoginDTO
import ie.setu.utils.authentication.JwtDTO
import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.decodeJWT
import ie.setu.utils.authentication.isCorrectPassword
import io.javalin.http.Context

/**
 * Singleton object for handling authentication-related operations.
 *
 * This object provides functionality for user login, authentication, and JWT validation.
 */
object AuthenticationController {
    private val userDao = UserDAO()

    /**
     * Handles user login and issues a JSON Web Token (JWT) upon successful authentication.
     *
     * @param ctx The context for handling the HTTP request and response.
     */
    fun login(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val userLoginDTO = mapper.readValue<UserLoginDTO>(ctx.body())

        val user = userDao.findByEmail(userLoginDTO.email)

        if (user == null) {
            ctx.status(401)
        }

        val isCorrectPassword = isCorrectPassword(userLoginDTO.password, user!!.passwordHash!!)

        if (isCorrectPassword) {
            val token = JwtProvider.provider.generateToken(user)
            ctx.json(JwtDTO(token))
            ctx.status(200)
        } else {
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
        } catch (e: Exception) {
            ctx.status(401)
        }
    }
}
