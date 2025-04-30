package com.kerry.mcpshortenurl.controller

import com.kerry.mcpshortenurl.model.ShortenUrlRequest
import com.kerry.mcpshortenurl.model.ShortenUrlResponse
import com.kerry.mcpshortenurl.service.UrlShortenerService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api")
class UrlShortenerController @Autowired constructor(
    private val urlShortenerService: UrlShortenerService
) {
    
    @PostMapping("/shorten")
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ResponseEntity<ShortenUrlResponse> {
        logger.info { "Received request to shorten URL: ${request.url}" }
        
        // Validate the URL (simple check)
        if(!request.url.startsWith("http://") && !request.url.startsWith("https://")) {
            logger.warn { "Invalid URL format: ${request.url}" }
            return ResponseEntity.badRequest().build()
        }
        
        // Shorten the URL
        val urlMapping = urlShortenerService.shortenUrl(request.url)
        
        // Build the complete shortened URL
        val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        val shortUrl = "$baseUrl/api/shorten/${urlMapping.shortKey}"
        
        logger.info { "URL shortened: ${request.url} -> $shortUrl" }
        
        return ResponseEntity.ok(
            ShortenUrlResponse(
                originalUrl = urlMapping.originalUrl,
                shortUrl = shortUrl
            )
        )
    }
    
    @GetMapping("/shorten/{shortKey}")
    fun redirectToOriginalUrl(@PathVariable shortKey: String): ResponseEntity<Any> {
        logger.info { "Received request to get original URL for key: $shortKey" }
        
        val originalUrl = urlShortenerService.getOriginalUrl(shortKey)
        
        return if(originalUrl != null) {
            logger.info { "Redirecting to original URL: $originalUrl" }
            ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build()
        } else {
            logger.warn { "No URL mapping found for key: $shortKey" }
            ResponseEntity.notFound().build()
        }
    }
}
