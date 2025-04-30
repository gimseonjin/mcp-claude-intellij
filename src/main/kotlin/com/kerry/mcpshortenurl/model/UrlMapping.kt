package com.kerry.mcpshortenurl.model

data class UrlMapping(
    val originalUrl: String,
    val shortKey: String
)

data class ShortenUrlRequest(
    val url: String
)

data class ShortenUrlResponse(
    val originalUrl: String,
    val shortUrl: String
)
