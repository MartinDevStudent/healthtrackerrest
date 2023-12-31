package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.repository.UserDAO
import ie.setu.domain.user.UserLoginDTO
import ie.setu.utils.authentication.JwtDTO
import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.isCorrectPassword
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse

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
            throw UnauthorizedResponse("User cannot login in specified email address")
        }

        val isCorrectPassword = isCorrectPassword(userLoginDTO.password, user.passwordHash!!)

        if (!isCorrectPassword) {
            throw UnauthorizedResponse("User cannot login in specified email address")
        }

        val token = JwtProvider.provider.generateToken(user)
        ctx.json(JwtDTO(token))
        ctx.status(200)
    }
}
