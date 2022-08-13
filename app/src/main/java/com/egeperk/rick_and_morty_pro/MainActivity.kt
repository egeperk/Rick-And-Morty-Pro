package com.egeperk.rick_and_morty_pro

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
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

        fun updateStatusBarColor(color: String){// Color must be in hexadecimal fromat
            val window = window;
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = Color.parseColor(color);
        }
    }
}