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
        return dsl.select(USERS.USERID, USERS.USERNAME, USERS.PASSWORDHASH)
            .from(USERS)
            .where(USERS.USERNAME.eq(userName))
            .fetchOne()?.map {
                User(
                    id = it[USERS.USERID],
                    username = it[USERS.USERNAME],
                    password = it[USERS.PASSWORDHASH]
                )
            }
    }

    fun saveUser(user: User): User {
        val record = dsl.insertInto(USERS, USERS.USERNAME, USERS.PASSWORDHASH, USERS.EMAIL, USERS.ROLE)
            .values(user.username, user.password, "d.ruzhkov7@gmail.com" + Random().nextInt(Integer.MAX_VALUE), "VIEWER")
            .returning(USERS.USERID, USERS.USERNAME, USERS.EMAIL)
            .fetchOne() ?: throw IllegalArgumentException()

        return User(
            id = record[USERS.USERID],
            username = record[USERS.USERNAME]
        )
    }
}