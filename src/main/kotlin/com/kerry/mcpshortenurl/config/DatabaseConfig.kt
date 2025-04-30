package com.kerry.mcpshortenurl.config

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("local_postgres")
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
class DatabaseConfig {

    @Bean
    fun database(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
}
