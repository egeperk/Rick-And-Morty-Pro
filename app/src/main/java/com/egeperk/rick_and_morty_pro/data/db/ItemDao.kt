package com.egeperk.rick_and_morty_pro.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertCharacter(character: Character)

    @Query("SELECT * FROM character_table")
    fun readAllCharacters(): Flow<List<Character>>

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertEpisode(episode: Episode)

    @Query("SELECT * FROM episode_table")
    fun readAllEpisodes(): Flow<List<Episode>>
}