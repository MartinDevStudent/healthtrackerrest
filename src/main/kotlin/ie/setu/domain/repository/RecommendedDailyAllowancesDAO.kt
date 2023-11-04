package ie.setu.domain.repository

import ie.setu.domain.RecommendedDailyAllowance
import ie.setu.domain.db.*
import ie.setu.utils.mapToRecommendedDailyAllowance
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RecommendedDailyAllowancesDAO {
    fun getAll(): RecommendedDailyAllowance?  {
        return transaction {
            RecommendedDailyAllowances
                .selectAll()
                .map{ mapToRecommendedDailyAllowance(it) }
                .firstOrNull()
        }
    }
}
