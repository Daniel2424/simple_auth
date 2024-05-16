package com.example.demo.entity.balance.controller

import com.example.demo.entity.balance.service.BalanceService
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class BalanceController(
    private val balanceService: BalanceService,
) {
}