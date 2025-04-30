package com.kerry.mcpshortenurl.service

import com.kerry.mcpshortenurl.model.UrlMapping
import com.kerry.mcpshortenurl.repository.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.SecureRandom
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@Service
class UrlShortenerService @Autowired constructor(
    private val urlRepository: UrlRepository
) {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val secureRandom = SecureRandom()
    
    fun shortenUrl(originalUrl: String): UrlMapping {
        logger.info { "Shortening URL: $originalUrl" }
        
        // Generate a 6-character random key that starts with a letter
        val shortKey = generateUniqueKey()
        
        // Create and save the mapping
        val urlMapping = UrlMapping(originalUrl, shortKey)
        return urlRepository.save(urlMapping)
    }
    
    fun getOriginalUrl(shortKey: String): String? {
        logger.info { "Getting original URL for key: $shortKey" }
        return urlRepository.findByShortKey(shortKey)?.originalUrl
    }
    
    private fun generateUniqueKey(): String {
        // Start with a letter
        val firstChar = charPool.filter { it.isLetter() }.random(Random(secureRandom.nextLong()))
        
        // Generate the rest of the characters (5 more)
        val restChars = (1..5)
            .map { charPool[secureRandom.nextInt(charPool.size)] }
            .joinToString("")
        
        val key = firstChar + restChars
        
        // Check if the key already exists, if so, try again (unlikely but possible)
        return if(urlRepository.findByShortKey(key) != null) {
            logger.info { "Generated key $key already exists, trying again" }
            generateUniqueKey()
        } else {
            key
        }
    }
}
