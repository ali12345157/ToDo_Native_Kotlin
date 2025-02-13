package com.route.todoc41

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.route.todoc41.database.MyDatabase
import com.route.todoc41.database.dao.TasksDao
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ActivityEditBinding
import java.text.SimpleDateFormat
import java.util.*

class Edit : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    private var taskId: Int = -1
    private var taskIsDone: Boolean = false
    private val calendar = Calendar.getInstance()
    private lateinit var tasksDao: TasksDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DAO
        tasksDao = MyDatabase.getInstance().tasksDao()

        // Get task data from intent
        taskId = intent.getIntExtra("task_id", -1)
        val taskTitle = intent.getStringExtra("task_title") ?: ""
        val taskDescription = intent.getStringExtra("task_description") ?: ""
        val taskDate = intent.getLongExtra("task_date", -1L)
        val taskTime = intent.getLongExtra("task_time", -1L)
        taskIsDone = intent.getBooleanExtra("task_is_done", false)

        // Log the task details
        Log.d("EditActivity", "ID: $taskId, Title: $taskTitle, Description: $taskDescription, Date: $taskDate, Time: $taskTime")

        // Pre-fill the fields
        binding.title.setText(taskTitle)
        binding.description.setText(taskDescription)

        if (taskDate != -1L) {
            binding.selectDateTv.text = formatDate(taskDate)
            calendar.timeInMillis = taskDate
        } else {
            binding.selectDateTv.text = getString(R.string.select_date)
        }

        if (taskTime != -1L) {
            binding.selectTimeTv.text = formatTime(taskTime)
        } else {
            binding.selectTimeTv.text = getString(R.string.select_time)
        }

        // Setup event listeners
        onSelectDateClick()
        onSelectTimeClick()
        onAddTaskClick()
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e("EditActivity", "Error formatting date", e)
            getString(R.string.invalid_date)
        }
    }

    private fun formatTime(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e("EditActivity", "Error formatting time", e)
            getString(R.string.invalid_time)
        }
    }

    private fun onSelectDateClick() {
        binding.selectDateTv.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.selectDateTv.text = formatDate(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun onSelectTimeClick() {
        binding.selectTimeTv.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.selectTimeTv.text = formatTime(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }
    }

    private fun onAddTaskClick() {
        binding.save.setOnClickListener {
            val updatedTitle = binding.title.text.toString()
            val updatedDescription = binding.description.text.toString()

            if (updatedTitle.isBlank() || updatedDescription.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = Task(
                id = taskId,
                title = updatedTitle,
                description = updatedDescription,
                date = calendar.timeInMillis,
                time = calendar.timeInMillis,
                isDone = taskIsDone
            )

            try {
                tasksDao.updateTask(updatedTask)
                Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("EditActivity", "Error updating task", e)
                Toast.makeText(this, "Error updating task!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
