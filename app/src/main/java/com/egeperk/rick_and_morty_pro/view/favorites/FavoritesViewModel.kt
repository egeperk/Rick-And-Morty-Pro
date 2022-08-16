package com.egeperk.rick_and_morty_pro.view.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.data.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: LocalRepository): ViewModel() {

    val readCharactersData: Flow<List<Character>> = repository.readCharactersData

    val readEpisodesData: Flow<List<Episode>> = repository.readAllEpisodesData

    suspend fun addCharacter(character: Character) {
            repository.addCharacter(character)

    }
    fun addEpisode(episode: Episode) {
        viewModelScope.launch {
            repository.addEpisode(episode)
        }
    }
}