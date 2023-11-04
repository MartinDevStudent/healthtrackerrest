package ie.setu.domain.repository

import ie.setu.domain.Ingredient
import ie.setu.domain.IngredientApiDTO
import ie.setu.domain.db.Ingredients
import ie.setu.utils.mapToIngredient
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.ArrayList

class IngredientDAO {

    /**
     * Retrieves a list of all ingredients stored in the system's database.
     *
     * This method queries the system's database to retrieve a list of all ingredients that have been
     * stored. It maps the database result rows to Ingredient objects and returns them as an ArrayList.
     *
     * @return An ArrayList containing all the ingredients stored in the system's database.
     */
    fun getAll(): ArrayList<Ingredient>  {
        val ingredientsList: ArrayList<Ingredient> = arrayListOf()
        transaction {
            Ingredients.selectAll().map {
                ingredientsList.add(mapToIngredient(it)) }
        }
        return ingredientsList
    }

    /**
     * Retrieves an ingredient by its unique identifier (ingredient ID).
     *
     * This method queries the system's database to retrieve a specific ingredient based on its unique
     * identifier (ingredient ID). If an ingredient with the specified ID is found in the system, it is
     * returned as an Ingredient object. If no matching ingredient is found, the method returns null.
     *
     * @param id The unique identifier (ingredient ID) of the ingredient to retrieve.
     * @return The Ingredient object if found, or null if no matching ingredient is found.
     */
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

    // TODO: Add insert to junction table
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
