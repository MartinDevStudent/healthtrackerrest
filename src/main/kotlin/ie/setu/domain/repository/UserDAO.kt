package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.collections.ArrayList


class UserDAO {

    fun getAll() : ArrayList<User>{
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it))
            }
        }
        return userList
    }

    fun findById(id: Int): User? {
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun findByEmail(email: String) :User? {
        return transaction {
            Users.select() {
                Users.email.lowerCase() eq email.lowercase()}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun save(user: User) {
    }

    fun delete(id: Int) {
    }

    fun update(id: Int, user: User) {
    }
}