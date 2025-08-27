package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import com.example.todos.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }
    install(CORS) {
        allowMethod(HttpMethod.Get); allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put); allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // tighten for production
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "unknown")))
            throw cause
        }
    }

    DatabaseFactory.init()
    configureRoutes()
}