package com.example.todos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface TodoRepository {
    fun all(): List<TodoDTO>
    fun get(id: Int): TodoDTO?
    fun create(req: CreateTodoDTO): TodoDTO
    fun update(id: Int, req: UpdateTodoDTO): TodoDTO?
    fun delete(id: Int): Boolean
}

class ExposedTodoRepository : TodoRepository {
    override fun all(): List<TodoDTO> = transaction {
        TodosTable.selectAll().orderBy(TodosTable.id).map { it.toTodoDTO() }
    }

    override fun get(id: Int): TodoDTO? = transaction {
        TodosTable.select { TodosTable.id eq id }.singleOrNull()?.toTodoDTO()
    }

    override fun create(req: CreateTodoDTO): TodoDTO = transaction {
        val insertedId = TodosTable.insertAndGetId {
            it[title] = req.title
        }.value
        get(insertedId)!!
    }

    override fun update(id: Int, req: UpdateTodoDTO): TodoDTO? = transaction {
        val updated = TodosTable.update({ TodosTable.id eq id }) {
            req.title?.let { t -> it[title] = t }
            req.completed?.let { c -> it[completed] = c }
        }
        if (updated == 0) null else get(id)
    }

    override fun delete(id: Int): Boolean = transaction {
        TodosTable.deleteWhere { TodosTable.id eq id } > 0
    }
}