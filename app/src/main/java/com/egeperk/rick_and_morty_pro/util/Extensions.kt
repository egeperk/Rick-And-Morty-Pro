package com.egeperk.rick_and_morty_pro.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.egeperk.rick_and_morty_pro.R
import com.egeperk.rick_and_morty_pro.util.Constants.DELAY_TIME


fun NavController.safeNavigate(directions: NavDirections) {
    try {
        //android.os.Handler().postDelayed({
            navigate(directions)
        //}, DELAY_TIME)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}

fun Activity.setStatusBarDark(view: View) {
    window?.statusBarColor = resources.getColor(R.color.app_black)
    WindowInsetsControllerCompat(window!!, view).isAppearanceLightStatusBars = false
}

fun Activity.setStatusBarLight(view: View) {
    window?.statusBarColor = resources.getColor(R.color.white)
    WindowInsetsControllerCompat(window!!, view).isAppearanceLightStatusBars = true
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasClicked = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasClicked = true
            }
        }
        hasClicked
    }
}

fun Activity.hasInternetConnection(): Boolean {
    val connectivityManager =
        application?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val hasConnection = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(hasConnection) ?: return false

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}