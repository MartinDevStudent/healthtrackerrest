package ie.setu.domain.repository

import ie.setu.domain.RecommendedDailyAllowance
import ie.setu.domain.db.*
import ie.setu.utils.mapToRecommendedDailyAllowance
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RecommendedDailyAllowancesDAO {
    /**
     * Retrieves a recommended daily allowance (RDA) from the database, or null if none are found.
     *
     * @return A [RecommendedDailyAllowance] object representing the RDA if found, or null if no RDAs exist.
     */
    fun getAll(): RecommendedDailyAllowance?  {
        return transaction {
            RecommendedDailyAllowances
                .selectAll()
                .map{ mapToRecommendedDailyAllowance(it) }
                .firstOrNull()
        }
    }
}
