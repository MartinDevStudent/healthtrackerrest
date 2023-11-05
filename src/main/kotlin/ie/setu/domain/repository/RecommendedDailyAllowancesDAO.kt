package ie.setu.domain.repository

import ie.setu.domain.RecommendedDailyAllowance
import ie.setu.domain.db.RecommendedDailyAllowances
import ie.setu.utils.mapToRecommendedDailyAllowance
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RecommendedDailyAllowancesDAO {
    /**
     * Retrieves a recommended daily allowance (RDA) from the database, or null if none are found.
     *
     * @return A [RecommendedDailyAllowance] object representing the RDA if found, or null if no RDAs exist.
     */
    fun get(): RecommendedDailyAllowance? {
        return transaction {
            RecommendedDailyAllowances
                .selectAll()
                .map { mapToRecommendedDailyAllowance(it) }
                .firstOrNull()
        }
    }

    /**
     * Saves a Recommended Daily Allowance (RDA) record to the database.
     *
     * @param recommendedDailyAllowance The [RecommendedDailyAllowance] object to be saved to the database.
     */
    fun save(recommendedDailyAllowance: RecommendedDailyAllowance) {
        return transaction {
            RecommendedDailyAllowances.insert {
                it[calories] = recommendedDailyAllowance.calories
                it[fatTotalG] = recommendedDailyAllowance.fatTotalG
                it[fatSaturatedG] = recommendedDailyAllowance.fatSaturatedG
                it[proteinG] = recommendedDailyAllowance.proteinG
                it[sodiumMg] = recommendedDailyAllowance.sodiumMg
                it[potassiumMg] = recommendedDailyAllowance.potassiumMg
                it[cholesterolMg] = recommendedDailyAllowance.cholesterolMg
                it[carbohydratesTotalG] = recommendedDailyAllowance.carbohydratesTotalG
                it[fiberG] = recommendedDailyAllowance.fiberG
                it[sugarG] = recommendedDailyAllowance.sugarG
            }
        }
    }
}
