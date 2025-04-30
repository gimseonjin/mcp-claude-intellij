package com.kerry.mcpshortenurl.repository

import com.kerry.mcpshortenurl.model.UrlMapping
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

interface UrlRepository {
    fun save(urlMapping: UrlMapping): UrlMapping
    fun findByShortKey(shortKey: String): UrlMapping?
}

@Repository
@Profile("local") // local 환경에서만 사용
class InMemoryUrlRepository: UrlRepository {
    private val storage = HashMap<String, UrlMapping>()

    override fun save(urlMapping: UrlMapping): UrlMapping {
        logger.info { "Saving URL mapping to in-memory storage: ${urlMapping.shortKey} -> ${urlMapping.originalUrl}" }
        storage[urlMapping.shortKey] = urlMapping
        return urlMapping
    }

    override fun findByShortKey(shortKey: String): UrlMapping? {
        logger.info { "Finding URL mapping from in-memory storage for key: $shortKey" }
        return storage[shortKey]
    }
}
