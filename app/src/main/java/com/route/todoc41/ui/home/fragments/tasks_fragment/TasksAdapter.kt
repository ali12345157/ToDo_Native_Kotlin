package com.route.todoc41.ui.home.fragments.tasks_fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.route.todoc41.ClickItem
import com.route.todoc41.R
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ItemTaskBinding
import com.route.todoc41.ui.util.getFormattedTime
import java.util.Calendar

class TasksAdapter( val listener: ClickItem) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
var clickItem:ClickItem?=null
    private var tasksList = mutableListOf<Task>()

    @SuppressLint("NotifyDataSetChanged")
    fun setTasksList(tasks: MutableList<Task>) {
        tasksList = tasks
        notifyDataSetChanged()
    }

   inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) { binding.root.setOnClickListener {
            listener.click(task)
        }
            binding.title.text = task.title
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.time
            val hr = calendar.get(Calendar.HOUR)
            val minutes = calendar.get(Calendar.MINUTE)
            binding.time.text = getFormattedTime(hr, minutes)

            updateState(task)

            binding.btnTaskIsDone.setOnClickListener {
                task.isDone = !task.isDone
                updateState(task)

            }

        }

        private fun updateState(task: Task) {
            if (task.isDone) {
                binding.btnTaskIsDone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                binding.btnTaskIsDone.text = "Done!"
                binding.btnTaskIsDone.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.green)
                binding.title.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                binding.draggingBar.imageTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.green)





            } else {

                binding.btnTaskIsDone.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check,
                    0,
                    0,
                    0
                )

            }


        }
    }

    // Adapter Methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun getItemCount(): Int = tasksList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasksList[position]
        holder.bind(task)
    }

    fun getTask(position: Int): Task {
        return tasksList[position]
    }


    fun removeItem(position: Int): Task {
        val task = tasksList.removeAt(position)
        notifyItemRemoved(position)
        return task
    }
}
