package com.egeperk.rick_and_morty_pro.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.egeperk.rick_and_morty_pro.data.db.ItemDatabase
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import kotlinx.coroutines.flow.Flow


class LocalRepository(private val db: ItemDatabase) {

    fun readLimitedCharactersData(): PagingSource<Int,Character> = db.itemDao().readLimitedCharacters()

    fun readAllCharactersData(): PagingSource<Int,Character> = db.itemDao().readAllCharacters()

    fun getCharacterCount(): Flow<Int> = db.itemDao().getCharacterCount()

    fun getCharacterById(id: String): Flow<Character> = db.itemDao().getCharacterById(id)

    suspend fun addCharacter(character: Character) = db.itemDao().insertCharacter(character)

    fun readLimitedEpisodeData(): PagingSource<Int,Episode> = db.itemDao().readLimitedEpisode()

    fun readAllEpisodesData(): PagingSource<Int,Episode> = db.itemDao().readAllEpisodes()

    fun getEpisodeById(id: String): Flow<Episode> = db.itemDao().getEpisodeById(id)

    fun getEpisodeCount(): Flow<Int> = db.itemDao().getEpisodeCount()

    suspend fun addEpisode(episode: Episode) = db.itemDao().insertEpisode(episode)

}