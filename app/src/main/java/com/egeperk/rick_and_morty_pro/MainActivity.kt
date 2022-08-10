package com.egeperk.rick_and_morty_pro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.egeperk.rick_and_morty_pro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding?>(
            this, R.layout.activity_main
        ).apply {

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.main_nav_view) as NavHostFragment
            val navController = navHostFragment.navController
            bottomNavBar.setupWithNavController(navController)
            
        }
    }
}