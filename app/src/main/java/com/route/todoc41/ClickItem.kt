package com.route.todoc41

import com.route.todoc41.database.entity.Task

fun interface ClickItem {
    fun click(task: Task)
}