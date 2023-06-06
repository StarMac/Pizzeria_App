package com.example.pizzeriaapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.databinding.ActivityAdminBinding
import com.example.pizzeriaapp.databinding.ActivityEmployeeBinding

class AdminActivity : BaseActivity<ActivityAdminBinding>(ActivityAdminBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigation()
    }

    private fun initNavigation(){
        val navController = findNavController(R.id.admin_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_admin_menu,
            R.id.navigation_users,
            R.id.navigation_ban_list,
            R.id.navigation_options
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.adminNavView.setupWithNavController(navController)
    }
}