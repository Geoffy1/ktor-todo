package com.example

import com.example.todos.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    val repo: TodoRepository = ExposedTodoRepository()

    routing {
        get("/health") { call.respond(mapOf("status" to "ok")) }

        route("/api/todos") {
            get {
                call.respond(repo.all())
            }
            post {
                val body = call.receive<CreateTodoDTO>()
                require(body.title.isNotBlank()) { "Title is required" }
                val created = repo.create(body)
                call.respond(HttpStatusCode.Created, created)
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val item = id?.let { repo.get(it) }
                if (item == null) call.respond(HttpStatusCode.NotFound) else call.respond(item)
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val body = call.receive<UpdateTodoDTO>()
                val updated = repo.update(id, body)
                if (updated == null) call.respond(HttpStatusCode.NotFound) else call.respond(updated)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (repo.delete(id)) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}