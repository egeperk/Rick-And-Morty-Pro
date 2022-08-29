package com.egeperk.rick_and_morty_pro.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: String?,
    val name: String?,
    val dimension: String?,
    val type: String?,
): Parcelable