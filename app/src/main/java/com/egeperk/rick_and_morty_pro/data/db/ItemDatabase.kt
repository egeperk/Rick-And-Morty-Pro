package com.egeperk.rick_and_morty_pro.data.db

import androidx.room.*
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode

@Database(entities = [Character::class,Episode::class], version = 1, exportSchema = false)
abstract class ItemDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao

}