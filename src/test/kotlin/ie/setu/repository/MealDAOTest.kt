package ie.setu.repository

import ie.setu.domain.db.*
import ie.setu.domain.repository.MealDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.meals
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class MealDAOTest {
    //retrieving some test data from Fixtures
    private val user1 = users[0]
    private val user2 = users[1]
    private val user3 = users[2]
    private val meal1 = meals[0]
    private val meal2 = meals[1]
    private val meal3 = meals[2]

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadMeals {

        @Test
        fun `get all meals from empty table returns none`() {
            transaction {

                //Arrange - create and setup userDAO object
                SchemaUtils.create(Meals)
                val mealDAO = MealDAO()

                //Act & Assert
                assertEquals(0, mealDAO.getAll().size)
            }
        }
        @Test
        fun `getting all meals from a populated table returns all rows`() {
            transaction {

                //Arrange - create and populate user and activity tables
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
            }
        }

        @Test
        fun `get meal by id that doesn't exist, results in no meal returned`() {
            transaction {

                //Arrange - create and populate meal table
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(null, mealDAO.findByMealId(4))
            }
        }

        @Test
        fun `get meal by id that exists, results in the correct meal returned`() {
            transaction {

                //Arrange - create and populate meal table
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(meal2, mealDAO.findByMealId(2))
            }
        }

        @Test
        fun `get meal by name that doesn't exists, results in null`() {
            transaction {

                //Arrange - create and populate meal table
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(null, mealDAO.findByMealName(meals[3].name))
            }
        }

        @Test
        fun `get meals by user id that doesn't exists, results in empty array`() {
            transaction {

                //Arrange - create and populate meal and user stable
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Assert list is empty
                assertEquals(0, mealDAO.findByUserId(-1).size)
            }
        }

        @Test
        fun `get meals by user id that exists, results in array with correct meals`() {
            transaction {

                //Arrange - create and populate meal, user and user meals table
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Assert list is empty
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
            }
        }
    }

    @Nested
    inner class CreateMeals {
        @Test
        fun `multiple meals added to table can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate table with three meals
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
                assertEquals(meal1, mealDAO.findByMealId(meal1.id))
                assertEquals(meal2, mealDAO.findByMealId(meal2.id))
                assertEquals(meal3, mealDAO.findByMealId(meal3.id))
            }
        }

        @Test
        fun `multiple meals associated with a user can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate users and meals tables
                populateUsersTable()
                val mealDAO = populateMealsTable()

                //Act - associate meals with user
                populateUsersMealsTable()

                //Assert - meals are associated with user
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
                assertEquals(meal1, mealDAO.findByUserId(user1.id)[0])
                assertEquals(meal2, mealDAO.findByUserId(user1.id)[1])
            }
        }
    }

    @Nested
    inner class DeleteActivities {
        @Test
        fun `deleting a non-existent meal in table results in no deletion`() {
            transaction {

                //Arrange - create and populate meal table
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
                mealDAO.delete(4)
                assertEquals(3, mealDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing meal in table results in meal being deleted`() {
            transaction {

                //Arrange - create and populate meal table
                val mealDAO = populateMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
                mealDAO.delete(meal3.id)
                assertEquals(2, mealDAO.getAll().size)
            }
        }

        @Test
        fun `deleting meal of a non-existent user in table results in no deletion`() {
            transaction {

                //Arrange - create and populate user and activity tables
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
                mealDAO.deleteByUserId(4)
                assertEquals(3, mealDAO.getAll().size)
            }
        }

        @Test
        fun `deleting meal associations of an existing user in table results in meal associations being deleted`() {
            transaction {

                //Arrange - create and populate user/ activity tables and associate meals with user
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Act & Assert
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
                mealDAO.deleteByUserId(user1.id)
                assertEquals(0, mealDAO.findByUserId(user1.id).size)
            }
        }

        @Test
        fun `deleting meal associations of an existing user in table does not delete the meals`() {
            transaction {

                //Arrange - create and populate user/ activity tables and associate meals with user
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Act & Assert
                assertEquals(3, mealDAO.getAll().size)
                mealDAO.deleteByUserId(user1.id)
                assertEquals(3, mealDAO.getAll().size)
            }
        }

        @Test
        fun `deleting meals association of a non-existent meal id results in no deletion`() {
            transaction {

                //Arrange - create and populate user/ activity tables and associate meals with user
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Act & Assert
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
                mealDAO.deleteUserMealByMealId(user1.id,4)
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
            }
        }

        @Test
        fun `deleting meal association of a user in table results in meal association being deleted`() {
            transaction {

                //Arrange - create and populate user/ activity tables and associate meals with user
                val mealDAO = populateMealsTable()
                populateUsersTable()
                populateUsersMealsTable()

                //Act & Assert
                assertEquals(2, mealDAO.findByUserId(user1.id).size)
                mealDAO.deleteUserMealByMealId(user1.id, meal1.id)
                assertEquals(1, mealDAO.findByUserId(user1.id).size)
                assertNotEquals(meal1, mealDAO.findByUserId(user1.id)[0])
            }
        }
    }

    internal fun populateMealsTable(): MealDAO {
        SchemaUtils.create(Meals)
        SchemaUtils.create(MealsIngredients)
        val mealDAO = MealDAO()
        mealDAO.save(meal1)
        mealDAO.save(meal2)
        mealDAO.save(meal3)
        return mealDAO
    }

    internal fun populateUsersTable(): UserDAO {
        SchemaUtils.create(Users)
        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)
        return userDAO
    }

    internal fun populateUsersMealsTable() {
        SchemaUtils.create(UsersMeals)
        val mealDAO = MealDAO()
        mealDAO.associateMealWithUser(user1.id, meal1.id)
        mealDAO.associateMealWithUser(user1.id, meal2.id)
    }
}