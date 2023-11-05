package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig {
    private val logger = KotlinLogging.logger {}

    fun getDbConnection(): Database {
        @Suppress("ktlint:standard:property-naming")
        val PGHOST = "flora.db.elephantsql.com"

        @Suppress("ktlint:standard:property-naming")
        val PGPORT = "5432"

        @Suppress("ktlint:standard:property-naming")
        val PGUSER = "tjigxssw"

        @Suppress("ktlint:standard:property-naming")
        val PGPASSWORD = "8I6NNydeiWPHagNDswJoZunVs5OT7r2h"

        @Suppress("ktlint:standard:property-naming")
        val PGDATABASE = "tjigxssw"

        // url format should be jdbc:postgresql://host:port/database
        val dbUrl = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        val dbConfig =
            Database.connect(
                url = dbUrl,
                driver = "org.postgresql.Driver",
                user = PGUSER,
                password = PGPASSWORD,
            )

        logger.info { "DbConfig name = " + dbConfig.name }
        logger.info { "DbConfig url = " + dbConfig.url }

        return dbConfig
    }
}
