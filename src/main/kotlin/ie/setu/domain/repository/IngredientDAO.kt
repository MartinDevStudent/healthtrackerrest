package ie.setu.domain.repository

import ie.setu.domain.Ingredient
import ie.setu.domain.db.Ingredients
import ie.setu.domain.db.Meals
import ie.setu.domain.db.MealsIngredients
import ie.setu.utils.mapToIngredient
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.and
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
    fun getAll(): ArrayList<Ingredient> {
        val ingredientsList: ArrayList<Ingredient> = arrayListOf()
        transaction {
            Ingredients.selectAll().map {
                ingredientsList.add(mapToIngredient(it))
            }
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
                .select { Ingredients.id eq id }
                .map { mapToIngredient(it) }
                .firstOrNull()
        }
    }

    /**
     * Retrieves a list of ingredients associated with a specific meal ID from the database.
     *
     * @param id The ID of the meal for which ingredients are to be retrieved.
     * @return An [ArrayList] of [Ingredient] objects associated with the specified meal, or an empty list if none are found.
     */
    fun findByMealId(id: Int): ArrayList<Ingredient> {
        val ingredientsList: ArrayList<Ingredient> = arrayListOf()
        transaction {
            Ingredients.innerJoin(MealsIngredients)
                .slice(Ingredients.columns)
                .select { MealsIngredients.meal eq id }
                .map { ingredientsList.add(mapToIngredient(it)) }
        }

        return ingredientsList
    }

    /**
     * Saves an ingredient to the database based on the provided [Ingredient] or retrieves it if it already exists.
     *
     * @param ingredient The [Ingredient] representing the ingredient's data to be saved or retrieved.
     * @return The ID of the ingredient, whether it was inserted as a new record or retrieved from the database.
     */
    fun save(ingredient: Ingredient): Int {
        val ingredientRow =
            transaction {
                Ingredients
                    .select {
                        (Ingredients.name eq ingredient.name) and (Ingredients.servingSizeG eq ingredient.servingSizeG)
                    }.singleOrNull()
            }

        if (ingredientRow == null) {
            return transaction {
                Ingredients.insert {
                    it[name] = ingredient.name
                    it[calories] = ingredient.calories
                    it[servingSizeG] = ingredient.servingSizeG
                    it[fatTotalG] = ingredient.fatTotalG
                    it[fatSaturatedG] = ingredient.fatSaturatedG
                    it[proteinG] = ingredient.proteinG
                    it[sodiumMg] = ingredient.sodiumMg
                    it[potassiumMg] = ingredient.potassiumMg
                    it[cholesterolMg] = ingredient.cholesterolMg
                    it[carbohydratesTotalG] = ingredient.carbohydratesTotalG
                    it[fiberG] = ingredient.fiberG
                    it[sugarG] = ingredient.sugarG
                } get Ingredients.id
            }.value
        } else {
            return mapToIngredient(ingredientRow).id
        }
    }

    /**
     * Associates an ingredient with a meal by their respective IDs and saves this association in the database.
     *
     * @param ingredientId The ID of the ingredient to be associated with the meal.
     * @param mealId The ID of the meal to which the ingredient is to be associated.
     */
    fun associateIngredientWithMeal(
        ingredientId: Int,
        mealId: Int,
    ) {
        return transaction {
            MealsIngredients.insert {
                it[ingredient] = EntityID(ingredientId, Ingredients)
                it[meal] = EntityID(mealId, Meals)
            } get MealsIngredients.id
        }
    }
}
