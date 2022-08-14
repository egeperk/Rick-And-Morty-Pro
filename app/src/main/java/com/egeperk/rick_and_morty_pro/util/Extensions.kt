package com.egeperk.rick_and_morty_pro.util

import android.app.Activity
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.egeperk.rick_and_morty_pro.R


fun NavController.safeNavigate(directions: NavDirections) {
    try {
        navigate(directions)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}

fun Activity.setStatusBarDark(view: View) {

    window?.statusBarColor = resources.getColor(R.color.app_black)
    WindowInsetsControllerCompat(window!!,view ).isAppearanceLightStatusBars = false

}

fun Activity.setStatusBarLight(view: View) {

    window?.statusBarColor = resources.getColor(R.color.white)
    WindowInsetsControllerCompat(window!!,view ).isAppearanceLightStatusBars = true

}