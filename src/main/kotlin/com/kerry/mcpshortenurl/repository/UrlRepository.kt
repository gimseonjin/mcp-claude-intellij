package com.kerry.mcpshortenurl.repository

import com.kerry.mcpshortenurl.model.UrlMapping
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

interface UrlRepository {
    fun save(urlMapping: UrlMapping): UrlMapping
    fun findByShortKey(shortKey: String): UrlMapping?
}

@Repository
class InMemoryUrlRepository: UrlRepository {
    private val storage = HashMap<String, UrlMapping>()

    override fun save(urlMapping: UrlMapping): UrlMapping {
        logger.info { "Saving URL mapping: ${urlMapping.shortKey} -> ${urlMapping.originalUrl}" }
        storage[urlMapping.shortKey] = urlMapping
        return urlMapping
    }

    override fun findByShortKey(shortKey: String): UrlMapping? {
        logger.info { "Finding URL mapping for key: $shortKey" }
        return storage[shortKey]
    }
}
