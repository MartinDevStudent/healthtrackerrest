package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.activities
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ActivityDAOTest {
    //retrieving some test data from Fixtures
    val user1 = users.get(0)
    val user2 = users.get(1)
    val user3 = users.get(2)
    val activity1 = activities.get(0)
    val activity2 = activities.get(1)
    val activity3 = activities.get(2)

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadActivities {
        @Test
        fun `getting all activities from a populated table returns all rows`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `get activity by id that doesn't exist, results in no activity returned`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(null, activityDAO.findByActivityId(4))
            }
        }

        @Test
        fun `get activity by id that exists, results in the correct activity returned`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(activity2, activityDAO.findByActivityId(2))
            }
        }

        @Test
        fun `get all activities from empty table returns none`() {
            transaction {

                //Arrange - create and setup userDAO object
                SchemaUtils.create(Activities)
                val activityDAO = ActivityDAO()

                //Act & Assert
                assertEquals(0, activityDAO.getAll().size)
            }
        }



        @Test
        fun `get activities by user id that doesn't exist, results in empty array`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(arrayListOf<Activity>(), activityDAO.findByUserId(4))
            }
        }

        @Test
        fun `get activities by user id that exists, results in array with correct activities returned`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(arrayListOf(activity1, activity3), activityDAO.findByUserId(2))
            }
        }
    }

    @Nested
    inner class CreateActivities {
        @Test
        fun `multiple activities added to table can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                assertEquals(activity1, activityDAO.findByActivityId(activity1.id))
                assertEquals(activity2, activityDAO.findByActivityId(activity2.id))
                assertEquals(activity3, activityDAO.findByActivityId(activity3.id))
            }
        }
    }

    @Nested
    inner class DeleteActivities {
        @Test
        fun `deleting a non-existant activity in table results in no deletion`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(4)
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing activity in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(activity3.id)
                assertEquals(2, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting activities of a non-existant user in table results in no deletion`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteByUserId(4)
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting activities of an existing user in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                activityDAO.deleteByUserId(activity2.id)
                assertEquals(1, activityDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating existing activity in table results in successful update`() {
            transaction {

                //Arrange - create and populate user and activity tables
                populateUserTable()
                val activityDAO = populateActivityTable()

                //Act & Assert
                val activity3Updated =  Activity(description = "Unicycling", duration = 90.0, calories = 150, started = DateTime("2020-06-24T05:59:27.258Z"), id = 3, userId = 2)
                activityDAO.update(activity3.id, activity3Updated)
                assertEquals(activity3Updated, activityDAO.findByActivityId(3))
            }
        }
            @Test
            fun `updating non-existant activity in table results in no updates`() {
                transaction {

                    //Arrange - create and populate user and activity tables
                    populateUserTable()
                    val activityDAO = populateActivityTable()

                    //Act & Assert
                    val activity4Updated =  Activity(description = "new activity", duration = 0.0, calories = 0, started = DateTime("2020-06-01T05:59:27.258Z"), id = 4, userId = 2)

                    activityDAO.update(4, activity4Updated)
                    assertEquals(null, activityDAO.findByActivityId(4))
                    assertEquals(3, activityDAO.getAll().size)
                }
            }
        }

    internal fun populateUserTable(): UserDAO{
        SchemaUtils.create(Users)
        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)
        return userDAO
    }
    internal fun populateActivityTable(): ActivityDAO {
        SchemaUtils.create(Activities)
        val activityDAO = ActivityDAO()
        activityDAO.save(activity1)
        activityDAO.save(activity2)
        activityDAO.save(activity3)
        return activityDAO
    }
}