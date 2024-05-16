package com.example.demo.entity.offer.controller

import com.example.demo.entity.auth.AuthService
import com.example.demo.entity.offer.models.Offer
import com.example.demo.entity.offer.service.OfferService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@GraphQLApi
@Component
class OfferController(
    private val authService: AuthService,
    private val offerService: OfferService,
) {
    @GraphQLMutation(name = "createOffer")
    fun createOffer(@GraphQLArgument(name = "offer") offer: Offer): Int {
        return offerService.createOffer(offer)
    }

    @GraphQLQuery(name = "getAllActiveOffers")
    fun getAllActiveOffers(): List<Offer> {
        return offerService.getAllActiveOffers()
    }

    @GraphQLQuery(name = "getAllPendingReviewOffers")
    fun getAllPendingReviewOffers(): List<Offer> {
        return offerService.getAllPendingReviewOffers()
    }
}