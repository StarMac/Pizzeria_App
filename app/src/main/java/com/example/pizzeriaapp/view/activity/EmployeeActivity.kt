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
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_menu,
            R.id.navigation_cart,
            R.id.navigation_orders,
            R.id.navigation_options
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.empNavView.setupWithNavController(navController)
    }
}