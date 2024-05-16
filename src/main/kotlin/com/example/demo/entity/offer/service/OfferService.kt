package com.example.demo.entity.offer.service

import com.example.demo.entity.canvas.service.S3Service
import com.example.demo.entity.offer.models.Offer
import com.example.demo.entity.offer.models.OfferStatus
import com.example.demo.entity.offer.repository.OfferRepository
import com.example.demo.entity.user.service.UserService
import org.springframework.stereotype.Service

@Service
class OfferService(
    private val offerRepository: OfferRepository,
    private val s3Service: S3Service,
    private val userService: UserService,
) {
    fun createOffer(offer: Offer): Int {
        return offerRepository.createOffer(offer)
    }

    fun getAllActiveOffers(): List<Offer> {
        val status = OfferStatus.ACTIVE
        val offers = offerRepository.getAllOffersByStatus(status)
        return addUrlsImagesToOffer(offers)
    }

    fun getAllPendingReviewOffers(): List<Offer> {
        val status = OfferStatus.PENDING_REVIEW
        val offers = offerRepository.getAllOffersByStatus(status)
        return addUrlsImagesToOffer(offers)
    }

    private fun addUrlsImagesToOffer(offers: List<Offer>): List<Offer> {
        return offers.map {
            val user = userService.findUserById(it.userId)
            val prefixPath = "offers/${user?.username}/offer_${it.offerId}"
            it.copy(
                urls = s3Service.listUrlImagesInFolder(prefixPath)
            )
        }
    }
}