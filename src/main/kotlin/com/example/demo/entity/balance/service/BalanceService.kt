package com.example.demo.entity.balance.service

import com.example.demo.entity.balance.repository.BalanceRepository
import org.springframework.stereotype.Service

@Service
class BalanceService(
    private val balanceRepository: BalanceRepository,
) {
}