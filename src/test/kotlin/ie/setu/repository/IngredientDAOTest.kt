package ie.setu.repository

import ie.setu.domain.Ingredient
import ie.setu.domain.db.Ingredients
import ie.setu.domain.db.Meals
import ie.setu.domain.db.MealsIngredients
import ie.setu.domain.repository.IngredientDAO
import ie.setu.domain.repository.MealDAO
import ie.setu.helpers.ingredients
import ie.setu.helpers.meals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IngredientDAOTest {
    // retrieving some test data from Fixtures
    private val ingredient1 = ingredients[0]
    private val ingredient2 = ingredients[1]
    private val ingredient3 = ingredients[2]
    private val meal1 = meals[0]
    private val meal2 = meals[1]
    private val meal3 = meals[2]

    companion object {
        // Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadIngredients {
        @Test
        fun `getting all ingredients from a populated table returns all rows`() {
            transaction {
                // Arrange - create and populate table with three users
                populateMealTable()
                val ingredientDAO = populateIngredientTable()

                // Act & Assert
                assertEquals(3, ingredientDAO.getAll().size)
            }
        }

        @Test
        fun `get ingredient by id that doesn't exist, results in no ingredient returned`() {
            transaction {
                // Arrange - create and populate table with three ingredients
                populateMealTable()
                val ingredientDAO = populateIngredientTable()

                // Act & Assert
                assertEquals(null, ingredientDAO.findByIngredientId(4))
            }
        }

        @Test
        fun `get all ingredients over empty table returns none`() {
            transaction {
                // Arrange - create and setup mealDAO object
                SchemaUtils.create(Ingredients)
                val ingredientDAO = IngredientDAO()

                // Act & Assert
                assertEquals(0, ingredientDAO.getAll().size)
            }
        }

        @Test
        fun `get ingredients by meal id that doesn't exist, results in no empty array returned`() {
            transaction {
                // Arrange - create and populate table with three ingredients
                populateMealTable()
                val ingredientDAO = populateIngredientTable()

                // Act & Assert
                assertEquals(ArrayList<Ingredient>(), ingredientDAO.findByMealId(Integer.MIN_VALUE))
            }
        }

        @Test
        fun `get ingredients by meal id that exists, results in correct ingredients returned`() {
            transaction {
                // Arrange - create and populate table with three ingredients
                populateMealTable()
                val ingredientDAO = populateIngredientTable()

                // Act & Assert
                assertEquals(arrayListOf(ingredient1), ingredientDAO.findByMealId(meal1.id))
            }
        }
    }

    @Nested
    inner class CreateUsers {
        @Test
        fun `multiple ingredients added to table can be retrieved successfully`() {
            transaction {
                // Arrange - create and populate table with three ingredients
                populateMealTable()
                val ingredientDAO = populateIngredientTable()

                // Act & Assert
                assertEquals(3, ingredientDAO.getAll().size)
                assertEquals(ingredient1, ingredientDAO.findByIngredientId(ingredient1.id))
                assertEquals(ingredient2, ingredientDAO.findByIngredientId(ingredient2.id))
                assertEquals(ingredient3, ingredientDAO.findByIngredientId(ingredient3.id))
            }
        }
    }

    internal fun populateMealTable() {
        SchemaUtils.create(Meals)
        SchemaUtils.create(MealsIngredients)
        val mealDao = MealDAO()
        mealDao.save(meal1)
        mealDao.save(meal2)
        mealDao.save(meal3)
    }

    internal fun populateIngredientTable(): IngredientDAO {
        SchemaUtils.create(Ingredients)
        val ingredientDAO = IngredientDAO()
        ingredientDAO.save(ingredient1)
        ingredientDAO.save(ingredient2)
        ingredientDAO.save(ingredient3)
        ingredientDAO.associateIngredientWithMeal(ingredient1.id, meal1.id)
        ingredientDAO.associateIngredientWithMeal(ingredient2.id, meal2.id)
        ingredientDAO.associateIngredientWithMeal(ingredient3.id, meal3.id)
        return ingredientDAO
    }
}
