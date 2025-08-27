package com.example

import com.example.todos.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.HttpStatusCode

fun Application.configureRouting() {
    val todoRepository: ExposedTodoRepository = ExposedTodoRepository()

    routing {
        route("/todos") {
            get {
                val todos = todoRepository.allTodos()
                call.respond(todos)
            }

            post {
                val todo = call.receive<Todo>()
                val newTodo = todoRepository.create(todo)
                if (newTodo != null) {
                    call.respond(newTodo)
                } else {
                    call.respondText("Failed to create todo", status = HttpStatusCode.InternalServerError)
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@get
                }
                val todo = todoRepository.allTodos().find { it.id == id }
                if (todo != null) {
                    call.respond(todo)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@put
                }
                val updatedTodo = call.receive<Todo>()
                val success = todoRepository.update(id, updatedTodo)
                if (success) {
                    call.respondText("Todo updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                    return@delete
                }
                val success = todoRepository.delete(id)
                if (success) {
                    call.respondText("Todo deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}