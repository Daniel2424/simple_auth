package com.example.demo.entity.offer.models

import java.math.BigDecimal

data class Offer(
    val offerId: Int? = null,
    val userId: Int,
    val name: String,
    val description: String,
    val address: String,
    val email: String,
    val phone: String,
    val price: BigDecimal,
    val productType: String,
    val status: OfferStatus? = null,
    val urls: List<String>? = null,
)

enum class OfferStatus {
    PENDING_REVIEW, ACTIVE, CANCELLED, ARCHIVE,
}