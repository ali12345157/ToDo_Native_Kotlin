package com.route.todoc41.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.route.todoc41.R
import com.route.todoc41.TasksFragment
import com.route.todoc41.database.entity.Task
import com.route.todoc41.databinding.ActivityHomeBinding
import com.route.todoc41.ui.home.fragments.AddTaskFragment
import com.route.todoc41.ui.home.fragments.SettingsFragment
import com.route.todoc41.Helper

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun attachBaseContext(newBase: android.content.Context) {
        val context = Helper.setLocale(newBase, "en")
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val isDarkMode = true // يمكن تغيير هذه القيمة ديناميكيًا
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        setContentView(binding.root)

        configureNavigation()
        configureFabClick()
    }


    private fun configureNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tasks -> {
                    showFragment(TasksFragment())
                    binding.title.text = getString(R.string.to_do_list)
                }
                R.id.settings -> {
                    showFragment(SettingsFragment())
                    binding.title.text = getString(R.string.settings)
                }
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.tasks
    }


    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .commit()
    }


    private fun configureFabClick() {
        binding.fabAddTask.setOnClickListener {
            val bottomSheet = AddTaskFragment()
            bottomSheet.show(supportFragmentManager, null)
            bottomSheet.onTaskAdded = AddTaskFragment.OnTaskAdded { task: Task ->
            }
        }
    }
}
