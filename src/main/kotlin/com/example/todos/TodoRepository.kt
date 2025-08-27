package com.example.todos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

data class Todo(
    val id: Int,
    val title: String,
    val done: Boolean,
    val createdAt: LocalDateTime
)

object Todos : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val done = bool("done")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

interface TodoRepository {
    fun allTodos(): List<Todo>
    fun create(todo: Todo): Todo?
    fun update(id: Int, todo: Todo): Boolean
    fun delete(id: Int): Boolean
}

class ExposedTodoRepository : TodoRepository {
    private fun toTodo(row: ResultRow): Todo {
        return Todo(
            id = row[Todos.id],
            title = row[Todos.title],
            done = row[Todos.done],
            createdAt = row[Todos.createdAt]
        )
    }

    override fun allTodos(): List<Todo> = transaction {
        Todos.selectAll().map { toTodo(it) }
    }

    override fun create(todo: Todo): Todo? = transaction {
        val id = Todos.insertAndGetId {
            it[title] = todo.title
            it[done] = todo.done
            it[createdAt] = LocalDateTime.now()
        }
        Todos.select { Todos.id eq id }.singleOrNull()?.let { toTodo(it) }
    }

    override fun update(id: Int, todo: Todo): Boolean = transaction {
        Todos.update({ Todos.id eq id }) {
            it[title] = todo.title
            it[done] = todo.done
        } > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Todos.deleteWhere { Todos.id eq id } > 0
    }
}