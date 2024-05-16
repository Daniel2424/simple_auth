package com.example.demo.entity.user.repository

import com.example.demo.entity.user.models.User
import com.yourpackage.generated.Tables
import com.yourpackage.generated.Tables.USERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepository(
    private val dsl: DSLContext,
) {

    fun findByUsername(userName: String?): User? {
        val user = dsl.select(USERS.USERID, USERS.FIRSTNAME, USERS.LASTNAME, USERS.EMAIL, USERS.USERNAME, USERS.PASSWORDHASH, USERS.ROLE)
            .from(USERS)
            .where(USERS.USERNAME.eq(userName))
            .fetchOne()?.map {
                User(
                    id = it[USERS.USERID],
                    firstName = it[USERS.FIRSTNAME],
                    lastName = it[USERS.LASTNAME],
                    email = it[USERS.EMAIL],
                    username = it[USERS.USERNAME],
                    password = it[USERS.PASSWORDHASH],
                    role = it[USERS.ROLE]
                )
            }
        return user
    }

    fun saveUser(user: User): User {
        val insertedRows = dsl.insertInto(USERS, USERS.FIRSTNAME, USERS.LASTNAME, USERS.USERNAME, USERS.PASSWORDHASH, USERS.EMAIL, USERS.ROLE)
            .values(user.firstName, user.lastName, user.username, user.password, user.email, "VIEWER")
            .execute()
        if (insertedRows == 0) throw IllegalArgumentException("Не удалось создать нового пользователя")

        return user
    }

    fun findById(userId: Int): User? {
        val user = dsl.select(USERS.USERID, USERS.FIRSTNAME, USERS.LASTNAME, USERS.EMAIL, USERS.USERNAME, USERS.PASSWORDHASH, USERS.ROLE)
            .from(USERS)
            .where(USERS.USERID.eq(userId))
            .fetchOne()?.map {
                User(
                    id = it[USERS.USERID],
                    firstName = it[USERS.FIRSTNAME],
                    lastName = it[USERS.LASTNAME],
                    email = it[USERS.EMAIL],
                    username = it[USERS.USERNAME],
                    password = it[USERS.PASSWORDHASH],
                    role = it[USERS.ROLE]
                )
            }
        return user
    }
}