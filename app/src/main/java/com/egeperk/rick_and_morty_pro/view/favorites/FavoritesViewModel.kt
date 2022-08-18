package com.egeperk.rick_and_morty_pro.view.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty_pro.data.db.ItemDao
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.data.repository.LocalRepository
import com.egeperk.rick_and_morty_pro.util.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: LocalRepository) :
    ViewModel() {

    private val _charResult = MutableStateFlow<PagingData<Character>>(PagingData.empty())
    val charResult = _charResult.asStateFlow()

    private val _episodeResult = MutableStateFlow<PagingData<Episode>>(PagingData.empty())
    val epiodeResult = _episodeResult.asStateFlow()

    val characterCount = repository.getCharacterCount()

    val episodeCount = repository.getEpisodeCount()


    fun readCharactersData(): StateFlow<PagingData<Character>> {
        viewModelScope.launch {
            val result = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                repository.readAllCharactersData()
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope).value
            _charResult.value = result
        }
        return charResult
    }

    fun readEpisodesData(): StateFlow<PagingData<Episode>> {
        viewModelScope.launch {
            val result = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                repository.readAllEpisodesData()
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope).value
            _episodeResult.value = result
        }
        return epiodeResult
    }

    val readLimitedCharactersData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedCharactersData()
    }.flow.cachedIn(viewModelScope)

    val readLimitedEpisodesData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedEpisodeData()
    }.flow.cachedIn(viewModelScope)

    suspend fun addCharacter(character: Character) {
        repository.addCharacter(character)
    }

    suspend fun addEpisode(episode: Episode) {
        repository.addEpisode(episode)
    }

}