package ie.setu.domain.repository

import ie.setu.domain.Ingredient
import ie.setu.domain.IngredientApiDTO
import ie.setu.domain.db.Ingredients
import ie.setu.utils.mapToIngredient
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.ArrayList

class IngredientDAO {

    fun getAll(): ArrayList<Ingredient>  {
        val ingredientsList: ArrayList<Ingredient> = arrayListOf()
        transaction {
            Ingredients.selectAll().map {
                ingredientsList.add(mapToIngredient(it)) }
        }
        return ingredientsList
    }

    fun findByIngredientId(id: Int): Ingredient? {
        return transaction {
            Ingredients
                .select() { Ingredients.id eq id }
                .map{ mapToIngredient(it) }
                .firstOrNull()
        }
    }

    fun findByMealId(mealId: Int): ArrayList<Ingredient> {
        return arrayListOf()
        //return ingredients.filter { it.mealId == mealId }
    }

    fun save (mealId: Int, dto: IngredientApiDTO) {
        return transaction {
            Ingredients.insert {
                it[name] = dto.name
                it[calories] = dto.calories
                it[servingSizeG] = dto.servingSizeG
                it[fatTotalG] = dto.fatTotalG
                it[fatSaturatedG] = dto.fatSaturatedG
                it[proteinG] = dto.proteinG
                it[sodiumMg] = dto.sodiumMg
                it[potassiumMg] = dto.potassiumMg
                it[cholesterolMg] = dto.cholesterolMg
                it[carbohydratesTotalG] = dto.carbohydratesTotalG
                it[fiberG] = dto.fiberG
                it[sugarG] = dto.sugarG
            } get Ingredients.id
        }
    }
}
