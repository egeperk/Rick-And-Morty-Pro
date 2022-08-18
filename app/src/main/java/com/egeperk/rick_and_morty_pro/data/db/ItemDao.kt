package com.egeperk.rick_and_morty_pro.data.db

import android.icu.number.IntegerWidth
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
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

    @Query("select * from character_table limit 4")
    fun readLimitedCharacters(): PagingSource<Int, Character>

    @Query("select * from character_table")
    fun readAllCharacters(): PagingSource<Int, Character>

    @Query("select * from character_table where id=:id")
    fun getCharacterById(id: String): Flow<Character>

    @Query("select count (id) from character_table")
    fun getCharacterCount(): Flow<Int>

    @Query("select * from episode_table limit 3")
    fun readLimitedEpisode(): PagingSource<Int, Episode>

    @Query("select * from episode_table")
    fun readAllEpisodes(): PagingSource<Int, Episode>

    @Query("select count (id) from episode_table")
    fun getEpisodeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    suspend fun insertEpisode(episode: Episode)

}