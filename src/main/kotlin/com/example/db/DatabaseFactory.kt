package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private var database: Database? = null   // was val, now var

    fun init() {
        if (database == null) {
            val config = HikariConfig().apply {
                jdbcUrl = System.getenv("JDBC_DATABASE_URL")
                    ?: "jdbc:postgresql://localhost:5432/ktor_todo"
                driverClassName = "org.postgresql.Driver"
                username = System.getenv("DB_USER") ?: "postgres"
                password = System.getenv("DB_PASSWORD") ?: "postgres"
                maximumPoolSize = 10
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
            database = Database.connect(HikariDataSource(config))
        }
    }
}
