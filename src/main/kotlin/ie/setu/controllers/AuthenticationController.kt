package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.JwtResponse
import io.javalin.http.Context
import javalinjwt.JavalinJWT


object AuthenticationController {
    fun generate(ctx: Context) {
        val mockUser = User(1,"Mocky McMockface", "mock@email.com","user")
        val token = JwtProvider.provider.generateToken(mockUser)
        ctx.json(JwtResponse(token))
    }

    fun validate(ctx: Context) {
        val decodedJWT = JavalinJWT.getTokenFromHeader(ctx)
            .flatMap { token: String? ->
                JwtProvider.provider.validateToken(
                    token
                )
            }
        if (!decodedJWT.isPresent) {
            ctx.status(401).result("Missing or invalid token")
        } else {
            ctx.result("Hi " + decodedJWT.get().getClaim("name").asString())
        }
    }
}