package com.egeperk.rick_and_morty_pro.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections


fun NavController.safeNavigate(directions: NavDirections) {
    try {
        navigate(directions)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}