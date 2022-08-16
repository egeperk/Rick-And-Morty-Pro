package com.egeperk.rick_and_morty_pro.data.repository

import com.egeperk.rick_and_morty_pro.data.db.ItemDatabase
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class LocalRepository(private val db: ItemDatabase) {

    val readCharactersData: Flow<List<Character>> = db.itemDao().readAllCharacters()

    val readAllEpisodesData: Flow<List<Episode>> = db.itemDao().readAllEpisodes()

    suspend fun addCharacter(character: Character) = db.itemDao().insertCharacter(character)

    suspend fun addEpisode(episode: Episode) = db.itemDao().insertEpisode(episode)

}