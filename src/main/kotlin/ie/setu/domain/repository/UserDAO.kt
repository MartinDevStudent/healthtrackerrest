package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
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
        return null
    }

    fun findByEmail(email: String) :User? {
        return null
        //return users.find { it.email.lowercase(Locale.getDefault()) == email.lowercase(Locale.getDefault()) }
    }

    fun save(user: User) {
    }

    fun delete(id: Int) {
    }

    fun update(id: Int, user: User) {
    }
}