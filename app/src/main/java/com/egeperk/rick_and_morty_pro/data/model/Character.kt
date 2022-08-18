package com.egeperk.rick_and_morty_pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_table")
data class Character(
    val id: String?,
    val name: String?,
    val image: String?,
    val status: String?,
    val gender: String?,
    val species: String?,
    val type: String?,
    val origin: String?,
    val location: String?,
    @PrimaryKey(autoGenerate = false)
    val pk: Int
)
