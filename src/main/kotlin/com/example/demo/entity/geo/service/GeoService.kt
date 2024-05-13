package com.example.demo.entity.geo.service

import GeoResponse
import com.example.demo.entity.geo.repository.GeoRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.lang.RuntimeException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class GeoService(
    private val geoRepository: GeoRepository,
) {

    fun getCoordinatesByAddress(address: String): String {
        val geoInfoAboutRegion = geoRepository.getCoordinatesByAddress(address)

        val coordinates = geoInfoAboutRegion?.response?.geoObjectCollection?.featureMember?.firstOrNull()?.geoObject?.point?.pos
        val point = coordinates?.split(" ")
        if (point == null || point.size != 2) throw RuntimeException("Координата не была подобрана по адресу")
        val a = point[0]
        val b = point[1]
        return "$b,$a"
    }
}