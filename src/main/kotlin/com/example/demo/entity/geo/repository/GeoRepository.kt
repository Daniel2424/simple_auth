package com.example.demo.entity.geo.repository

import GeoResponse
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class GeoRepository {
    private val restTemplate = RestTemplate()
    private val apiGeoKey = "66bbcc68-2fd0-4b86-88af-5e69bc6e0f6d"

    fun getCoordinatesByAddress(address: String): GeoResponse? {
        val url = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=$apiGeoKey&geocode=$address"
        return restTemplate.getForObject(url, GeoResponse::class.java)
    }
}