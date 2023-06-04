package com.example.pizzeriaapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.databinding.ActivityEmployeeBinding

class EmployeeActivity : BaseActivity<ActivityEmployeeBinding>(ActivityEmployeeBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigation()
    }

    private fun initNavigation(){
        val navController = findNavController(R.id.emp_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_emp_orders,
            R.id.navigation_orders_history,
            R.id.navigation_options
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.empNavView.setupWithNavController(navController)
    }
}