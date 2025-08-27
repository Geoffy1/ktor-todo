package com.example.todos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface TodoRepository {
    fun all(): List<TodoDTO>
    fun get(id: Int): TodoDTO?
    fun create(req: CreateTodoDTO): TodoDTO
    fun update(id: Int, req: UpdateTodoDTO): TodoDTO?
    fun delete(id: Int): Boolean
}

class ExposedTodoRepository : TodoRepository {

    override fun all(): List<TodoDTO> = transaction {
        TodosTable.selectAll()
            .orderBy(TodosTable.id to SortOrder.ASC)
            .map { it.toTodoDTO() }
    }

    override fun get(id: Int): TodoDTO? = transaction {
        TodosTable.select { TodosTable.id eq id }
            .singleOrNull()
            ?.toTodoDTO()
    }

    override fun create(req: CreateTodoDTO): TodoDTO = transaction {
        val insertedId = TodosTable.insertAndGetId { row ->
            row[title] = req.title
            row[completed] = false
        }.value
        get(insertedId)!!
    }

    override fun update(id: Int, req: UpdateTodoDTO): TodoDTO? = transaction {
        val updatedRows = TodosTable.update({ TodosTable.id eq id }) { row ->
            if (req.title != null) row[title] = req.title
            if (req.completed != null) row[completed] = req.completed
        }
        if (updatedRows == 0) null else get(id)
    }

    override fun delete(id: Int): Boolean = transaction {
        TodosTable.deleteWhere { TodosTable.id eq id } > 0
    }
}
