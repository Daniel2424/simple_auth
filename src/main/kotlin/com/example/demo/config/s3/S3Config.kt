package com.example.demo.config.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class S3Config {

    @Value("\${accessKey}")
    private val accessKey: String? = null

    @Value("\${secret}")
    private val secret: String? = null

    @Value("\${region}")
    private val region: String? = null

    @Value("\${endpoint}")
    private val endpoint: String? = null

    @Bean
    fun s3(): AmazonS3 {
        val awsCredentials = BasicAWSCredentials(accessKey, secret)
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
    }
}