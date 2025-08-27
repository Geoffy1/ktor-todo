package com.example.todos

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TodosTable : IntIdTable("todos") {
    val title = varchar("title", 255)
    val completed = bool("completed").default(false)
}

@Serializable
data class TodoDTO(val id: Int, val title: String, val completed: Boolean)

@Serializable
data class CreateTodoDTO(val title: String)

@Serializable
data class UpdateTodoDTO(val title: String? = null, val completed: Boolean? = null)

fun ResultRow.toTodoDTO() = TodoDTO(
    id = this[TodosTable.id].value,
    title = this[TodosTable.title],
    completed = this[TodosTable.completed]
)