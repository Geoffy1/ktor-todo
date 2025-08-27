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

        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = jdbcUrl
            username = dbUser
            password = dbPass
            maximumPoolSize = 10
            isAutoCommit = false
            // optional: transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        // create datasource once and never reassign a val
        val dataSource = HikariDataSource(hikariConfig)

        // run Flyway migrations
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()

        // connect Exposed to the datasource
        Database.connect(dataSource)
    }
}
