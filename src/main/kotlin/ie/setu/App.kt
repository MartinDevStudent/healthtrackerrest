package ie.setu

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig

/**
 * Entry point for the application that initializes database and web service configurations.
 *
 * This function is the entry point for the application. It initializes the database connection
 * using [DbConfig] and starts the Javalin web service using [JavalinConfig].
 *
 * Example Usage:
 * ```kotlin
 * main()
 * ```
 *
 * @see DbConfig
 * @see JavalinConfig
 */
fun main() {
    DbConfig().getDbConnection()
    JavalinConfig().startJavalinService()
}
