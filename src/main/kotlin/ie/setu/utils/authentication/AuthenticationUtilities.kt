package ie.setu.utils.authentication

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.http.Context
import javalinjwt.JavalinJWT


fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

fun isCorrectPassword(
    password: String,
    passwordHash: String
): Boolean {
    val result = BCrypt.verifyer().verify(password.toCharArray(), passwordHash)
    return result.verified
}

fun decodeJWT(ctx: Context): DecodedJWT {
    return JavalinJWT.getDecodedFromContext(ctx)
}