package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.collections.ArrayList

class UserDAO {
    /**
     * Retrieves and returns a list of all users from the database.
     *
     * @return An ArrayList of User objects representing all users in the database.
     */
    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it))
            }
        }
        return userList
    }

    /**
     * Finds and returns a user by their unique ID.
     *
     * @param id The unique ID of the user to find.
     * @return A User object representing the found user, or null if not found.
     */
    fun findById(id: Int): User? {
        return transaction {
            Users.select {
                Users.id eq id
            }
                .map { mapToUser(it) }
                .firstOrNull()
        }
    }

    /**
     * Finds and returns a user by their email address (case-insensitive).
     *
     * @param email The email address of the user to find.
     * @return A User object representing the found user, or null if not found.
     */
    fun findByEmail(email: String): User? {
        return transaction {
            Users.select {
                Users.email.lowerCase() eq email.lowercase()
            }
                .map { mapToUser(it) }
                .firstOrNull()
        }
    }

    /**
     * Saves a new user to the database.
     *
     * @param user The User object to be added to the database.
     * @return The ID of the newly created user in the database, or null if the insertion fails.
     */
    fun save(user: User): Int {
        return transaction {
            Users.insert {
                it[name] = user.name
                it[email] = user.email
                it[level] = user.level
                it[passwordHash] = user.passwordHash!!
            } get Users.id
        }.value
    }

    /**
     * Deletes a user from the database by their unique ID.
     *
     * @param id The unique ID of the user to be deleted.
     * @return The number of rows affected (0 or 1) indicating the success of deletion.
     */
    fun delete(id: Int): Int {
        return transaction {
            Users.deleteWhere {
                Users.id eq id
            }
        }
    }

    /**
     * Updates an existing user in the database by their unique ID.
     *
     * @param id The unique ID of the user to be updated.
     * @param user The updated User object with new data.
     * @return The number of rows affected (0 or 1) indicating the success of the update.
     */
    fun update(
        id: Int,
        user: User,
    ): Int {
        return transaction {
            Users.update({
                Users.id eq id
            }) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
}
