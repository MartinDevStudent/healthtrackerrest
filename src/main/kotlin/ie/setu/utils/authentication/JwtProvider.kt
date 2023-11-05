package ie.setu.utils.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import ie.setu.domain.User
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import javalinjwt.JavalinJWT

/**
 * Object responsible for JWT (JSON Web Token) provider configuration and handling.
 */
object JwtProvider {
    // 1. Define the JWT signing algorithm using HMAC256 and a secret key.
    val algorithm = Algorithm.HMAC256("very_secret")

    // 2. Define a JWT generator to create JWTs for user data.
    val generator: JWTGenerator<User> =
        JWTGenerator<User> { user: User, alg: Algorithm? ->
            val token: JWTCreator.Builder =
                JWT.create()
                    .withClaim("name", user.name)
                    .withClaim("level", user.level)
            token.sign(alg)
        }

    // 3. Define a JWT verifier to validate incoming JWTs using the configured algorithm.
    val verifier = JWT.require(algorithm).build()

    // 4. Create a JWTProvider with the defined algorithm, generator, and verifier.
    val provider: JWTProvider<User> = JWTProvider(algorithm, generator, verifier)

    // 5. Define a JWT header decode handler for Javalin using the JWT provider.
    val decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider)
}
