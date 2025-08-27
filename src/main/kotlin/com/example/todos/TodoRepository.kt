package com.example.todos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TodoRepository {

    fun allTodos(): List<Todo> = transaction {
        Todos.selectAll().map { toTodo(it) }
    }

    fun todoById(id: Int): Todo? = transaction {
        Todos
            .selectAll()
            .where { Todos.id eq id }
            .singleOrNull()
            ?.let { toTodo(it) }
    }

    fun addTodo(title: String, done: Boolean): Todo = transaction {
        val id = Todos.insertAndGetId {
            it[Todos.title] = title
            it[Todos.done] = done
        }.value
        Todo(id, title, done)
    }

    fun updateTodo(id: Int, title: String, done: Boolean): Boolean = transaction {
        val updatedRows = Todos.update({ Todos.id eq id }) {
            it[Todos.title] = title
            it[Todos.done] = done
        }
        updatedRows > 0
    }

    fun deleteTodo(id: Int): Boolean = transaction {
        val deletedRows = Todos.deleteWhere { Todos.id eq id }
        deletedRows > 0
    }

    private fun toTodo(row: ResultRow): Todo =
        Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            done = row[Todos.done]
        )
}