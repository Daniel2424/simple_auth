package com.example.demo.entity.geo.repository

import GeoResponse
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class GeoRepository {
    private val restTemplate = RestTemplate()
    private val apiGeoKey = ""

    fun getCoordinatesByAddress(address: String): GeoResponse? {
        val url = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=$apiGeoKey&geocode=$address"
        return restTemplate.getForObject(url, GeoResponse::class.java)
    }
}