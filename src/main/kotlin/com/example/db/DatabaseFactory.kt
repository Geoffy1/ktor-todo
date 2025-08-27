package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init() {
        val jdbcUrl = System.getenv("DB_JDBC_URL") ?: "jdbc:postgresql://localhost:5432/todos"
        val dbUser = System.getenv("DB_USER") ?: "postgres"
        val dbPass = System.getenv("DB_PASS") ?: "postgres"

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = jdbcUrl
            username = dbUser
            password = dbPass
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val ds = HikariDataSource(config)
        Flyway.configure().dataSource(ds).locations("classpath:db/migration").load().migrate()
        Database.connect(ds)
    }
}
