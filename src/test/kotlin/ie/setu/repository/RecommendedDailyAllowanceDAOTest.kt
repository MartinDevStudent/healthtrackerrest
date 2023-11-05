package ie.setu.repository

import ie.setu.domain.db.RecommendedDailyAllowances
import ie.setu.domain.repository.RecommendedDailyAllowancesDAO
import ie.setu.helpers.recommendedDailyAllowance
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RecommendedDailyAllowanceDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadRecommendedDailyAllowances {
        @Test
        fun `getting recommended daily allowance from a populated table returns correct result`() {
            transaction {

                //Arrange - create and populate recommended daily allowancess tables
                val recommendedDailyAllowancesDAO = populateRecommendedDailyAllowancesTable()

                //Act & Assert
                assertEquals(recommendedDailyAllowance, recommendedDailyAllowancesDAO.get())
            }
        }

        @Test
        fun `get recommended daily allowance from empty table returns null`() {
            transaction {

                //Arrange - create and setup activityDAO object
                SchemaUtils.create(RecommendedDailyAllowances)
                val recommendedDailyAllowancesDAO = RecommendedDailyAllowancesDAO()

                //Act & Assert
                assertEquals(null, recommendedDailyAllowancesDAO.get())
            }
        }
    }

    internal fun populateRecommendedDailyAllowancesTable(): RecommendedDailyAllowancesDAO {
        SchemaUtils.create(RecommendedDailyAllowances)
        val recommendedDailyAllowancesDAO = RecommendedDailyAllowancesDAO()
        recommendedDailyAllowancesDAO.save(recommendedDailyAllowance)
        return recommendedDailyAllowancesDAO
    }
}