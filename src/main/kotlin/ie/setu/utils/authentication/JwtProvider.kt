package ie.setu.utils.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import ie.setu.domain.User
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import javalinjwt.JavalinJWT




object JwtProvider {
    //1.
    val algorithm = Algorithm.HMAC256("very_secret")

    //2.
    val generator: JWTGenerator<User> = JWTGenerator<User> { user: User, alg: Algorithm? ->
        val token: JWTCreator.Builder = JWT.create()
            .withClaim("name", user.name)
            .withClaim("level", user.level)
        token.sign(alg)
    }

    //3.
    val verifier = JWT.require(algorithm).build()

    //4.
    val provider: JWTProvider<User> = JWTProvider(algorithm, generator, verifier)
    val decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider)
}

