package com.example.demo.entity.offer.repository

import com.example.demo.entity.offer.models.Offer
import com.example.demo.entity.offer.models.OfferStatus
import com.yourpackage.generated.Tables.OFFERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OfferRepository(
    private val dslContext: DSLContext,
) {
    @Transactional
    fun createOffer(offer: Offer): Int {
        val record = dslContext
            .insertInto(OFFERS, OFFERS.NAME, OFFERS.USER_ID, OFFERS.DESCRIPTION, OFFERS.ADDRESS, OFFERS.EMAIL,
                OFFERS.PHONE, OFFERS.PRICE, OFFERS.PRODUCT_TYPE, OFFERS.STATUS)
            .values(
                offer.name, offer.userId, offer.description, offer.address, offer.email, offer.phone,
                offer.price, offer.productType, "PENDING_REVIEW")
            .returning(OFFERS.OFFER_ID)
            .fetchOne()

        // Проверяем, что результат не null и возвращаем ID
        return record?.offerId ?: throw IllegalStateException("Failed to insert offer")
    }

    @Transactional(readOnly = true)
    fun getAllOffersByStatus(offerStatus: OfferStatus): List<Offer> {
        return dslContext.selectFrom(OFFERS)
            .where(OFFERS.STATUS.eq(offerStatus.name))
            .fetch()
            .map {
                Offer(
                    offerId = it.offerId,
                    userId = it.userId,
                    name = it.name,
                    description = it.description,
                    address = it.address,
                    email = it.email,
                    phone = it.phone,
                    price = it.price,
                    productType = it.productType,
                    status =  OfferStatus.valueOf(it.status)
                )
            }
    }
}