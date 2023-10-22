package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.ArrayList


class UserDAO {

    fun getAll(): ArrayList<User>{
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

    fun findByEmail(email: String): User? {
        return transaction {
            Users.select() {
                Users.email.lowerCase() eq email.lowercase()}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun save(user: User): Int? {
        return transaction {
            Users.insert {
                it[name] = user.name
                it[email] = user.email
                it[level] = user.level
                it[passwordHash] = user.passwordHash!!
            } get Users.id
        }
    }

    fun delete(id: Int): Int {
        return transaction{
            Users.deleteWhere{
                Users.id eq id
            }
        }
    }

    fun update(id: Int, user: User): Int {
        return transaction {
            Users.update ({
                Users.id eq id}) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }
}