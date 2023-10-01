package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.utils.authentication.JwtProvider
import ie.setu.utils.authentication.JwtResponse
import io.javalin.http.Context
import javalinjwt.JavalinJWT

object AuthenticationController {
    fun generate(ctx: Context) {
        val mockUser = User(1, "Mocky McMockface", "mock@email.com", "user")
        val token = JwtProvider.provider.generateToken(mockUser)
        ctx.json(JwtResponse(token))
    }

    fun validate(ctx: Context) {
        println("test")
        val decodedJWT = JavalinJWT.getDecodedFromContext(ctx)
        ctx.result("Hi " + decodedJWT.getClaim("name").asString())
    }
}
