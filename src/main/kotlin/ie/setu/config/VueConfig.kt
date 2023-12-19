package ie.setu.config

import ie.setu.utils.authentication.decodeJWT
import io.javalin.http.Context
import io.javalin.vue.JavalinVueConfig

fun JavalinVueConfig.configure() {
    this.vueAppName = "app"
    this.stateFunction = { ctx -> mapOf("user" to currentUser(ctx)) }
}

private fun currentUser(ctx: Context): String? {
    return if (ctx.basicAuthCredentials() != null) decodeJWT(ctx).getClaim("name").asString() else null
}