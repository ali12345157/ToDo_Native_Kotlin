package com.route.todoc41

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.route.todoc41.database.MyDatabase
import com.route.todoc41.database.dao.TasksDao
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.FragmentTasksBinding
import com.route.todoc41.ui.home.fragments.tasks_fragment.TasksAdapter
import com.route.todoc41.ui.util.clearTime
import java.util.Calendar

class TasksFragment : Fragment(), ClickItem {
    private lateinit var binding: FragmentTasksBinding
    private lateinit var dao: TasksDao
    private val adapter = TasksAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = MyDatabase.getInstance().tasksDao()
        initRecyclerView()
        initCalendarView()
    }

    override fun click(task: Task) {
        val intent = Intent(requireContext(), Edit::class.java)
        intent.putExtra("task_id", task.id)
        intent.putExtra("task_title", task.title)
        intent.putExtra("task_description", task.description)
        intent.putExtra("task_date", task.date)
        intent.putExtra("task_time", task.time)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        binding.rvTasks.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
    }

    private fun initCalendarView() {
        binding.calendarView.selectedDate = CalendarDay.today()
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, date.day)
            calendar.clearTime()

            dao.getAllTasksByDate(calendar.timeInMillis).observe(viewLifecycleOwner) { tasks ->
                adapter.setTasksList(tasks.toMutableList())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dao.getAllTasksByDate(getSelectedDate().timeInMillis).observe(viewLifecycleOwner) { tasks ->
            adapter.setTasksList(tasks.toMutableList())
        }
    }

    private fun getSelectedDate(): Calendar {
        val calendar = Calendar.getInstance()
        binding.calendarView.selectedDate?.let { date ->
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, date.day)
        }
        calendar.clearTime()
        return calendar
    }

    private val itemTouchHelper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val deletedTask = adapter.getTask(position)

            adapter.removeItem(position)
            dao.deleteTask(deletedTask)

            Snackbar.make(binding.rvTasks, "Task deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    dao.insertNewTask(deletedTask)
                }.show()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val paint = Paint().apply { color = Color.RED }
            val deleteIcon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)

            if (dX < 0) { // Swipe left
                c.drawRect(
                    itemView.right + dX, itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat(),
                    paint
                )
                deleteIcon?.setBounds(
                    itemView.right - 100, itemView.top + (itemView.height / 4),
                    itemView.right - 20, itemView.bottom - (itemView.height / 4)
                )
            } else { // Swipe right
                c.drawRect(
                    itemView.left.toFloat(), itemView.top.toFloat(),
                    itemView.left + dX, itemView.bottom.toFloat(),
                    paint
                )
                deleteIcon?.setBounds(
                    itemView.left + 20, itemView.top + (itemView.height / 4),
                    itemView.left + 100, itemView.bottom - (itemView.height / 4)
                )
            }
            deleteIcon?.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    })
}
