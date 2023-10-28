package ie.setu.utils.authentication

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import javalinjwt.JavalinJWT

/**
 * Hashes a plaintext password using the BCrypt algorithm with default parameters.
 *
 * @param password The plaintext password to be hashed.
 * @return The hashed password as a string.
 */
fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

/**
 * Verifies if a plaintext password matches a BCrypt hashed password.
 *
 * @param password The plaintext password to be verified.
 * @param passwordHash The BCrypt hashed password to compare against.
 * @return `true` if the plaintext password matches the hashed password, `false` otherwise.
 */
fun isCorrectPassword(
    password: String,
    passwordHash: String
): Boolean {
    val result = BCrypt.verifyer().verify(password.toCharArray(), passwordHash)
    return result.verified
}

/**
 * Decodes a JSON Web Token (JWT) from the Javalin context.
 *
 * @param ctx The Javalin context containing the JWT to be decoded.
 * @return The decoded JWT as a DecodedJWT object.
 */
fun decodeJWT(ctx: Context): DecodedJWT {
    return JavalinJWT.getDecodedFromContext(ctx)
}