package com.route.todoc41.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.route.todoc41.database.entity.Task

@Dao
interface TasksDao {

    @Insert
    fun insertNewTask(task: Task): Long

    @Delete
    fun deleteTask(task: Task): Int

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task WHERE date = :date")
    fun getAllTasksByDate(date: Long): LiveData<List<Task>>


    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: Int): Task?

    @Query("SELECT * FROM task WHERE isDone = 0")
    fun getUnCompletedTasks(): List<Task>


    @Query("DELETE FROM task WHERE id = :taskId")
    fun deleteTaskById(taskId: Long): Int
}
