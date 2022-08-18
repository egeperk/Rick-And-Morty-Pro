package com.egeperk.rick_and_morty_pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_table")
data class Episode(
    val id: String?,
    val name: String?,
    val episode: String?,
    val air_date: String?,
    @PrimaryKey(autoGenerate = false)
    val pk: Int
)
