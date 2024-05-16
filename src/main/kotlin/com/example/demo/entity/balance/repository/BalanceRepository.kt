package com.example.demo.entity.balance.repository

import com.example.demo.entity.balance.models.Balance
import com.yourpackage.generated.Tables.BALANCES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class BalanceRepository(
    private val dslContext: DSLContext,
) {

    @Transactional
    fun getBalanceOrCreateIfNotExists(userId: Int): Balance {
        val balanceRecord = dslContext
            .selectFrom(BALANCES)
            .where(BALANCES.USERID.eq(userId))
            .fetchOne()

        if (balanceRecord != null) {
            return Balance(
                balanceId = balanceRecord.balanceid,
                userId = balanceRecord.userid,
                balance = balanceRecord.balance,
                currency = balanceRecord.currency
            )
        }

        val newBalance = Balance(
            userId = userId,
            balance = BigDecimal.ZERO,
            currency = "RUB"
        )

        dslContext.insertInto(BALANCES)
            .set(BALANCES.USERID, newBalance.userId)
            .set(BALANCES.BALANCE, newBalance.balance)
            .set(BALANCES.CURRENCY, newBalance.currency)
            .execute()

        return newBalance
    }
}