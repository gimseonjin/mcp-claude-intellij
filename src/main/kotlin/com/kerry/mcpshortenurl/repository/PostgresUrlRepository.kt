package com.kerry.mcpshortenurl.repository

import com.kerry.mcpshortenurl.model.UrlMapping
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

// Exposed 테이블 정의
object UrlMappings : IntIdTable() {
    val originalUrl = varchar("original_url", 2048)
    val shortKey = varchar("short_key", 64).uniqueIndex()
    val createdAt = long("created_at").default(System.currentTimeMillis())
}

@Repository
@Profile("local_postgres")
class PostgresUrlRepository(private val database: Database) : UrlRepository {
    
    init {
        // 애플리케이션 시작 시 테이블 생성
        transaction(database) {
            SchemaUtils.create(UrlMappings)
        }
    }

    override fun save(urlMapping: UrlMapping): UrlMapping {
        logger.info { "Saving URL mapping to PostgreSQL: ${urlMapping.shortKey} -> ${urlMapping.originalUrl}" }
        
        transaction(database) {
            UrlMappings.insertIgnore {
                it[originalUrl] = urlMapping.originalUrl
                it[shortKey] = urlMapping.shortKey
            }
        }
        
        return urlMapping
    }

    override fun findByShortKey(shortKey: String): UrlMapping? {
        logger.info { "Finding URL mapping from PostgreSQL for key: $shortKey" }
        
        return transaction(database) {
            UrlMappings.select { UrlMappings.shortKey eq shortKey }
                .map { UrlMapping(it[UrlMappings.originalUrl], it[UrlMappings.shortKey]) }
                .singleOrNull()
        }
    }
}
