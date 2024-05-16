package com.example.demo.entity.balance.models

import java.math.BigDecimal

data class Balance(
    val balanceId: Int? = null,
    val userId: Int,
    val currency: String,
    val balance: BigDecimal,
)