package com.egeperk.rick_and_morty_pro.view.favorites

import androidx.lifecycle.*
import androidx.paging.*
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

    private val _character :MutableStateFlow<Character>? = null
    val character = _character?.asStateFlow()

    private val _episodeResult = MutableStateFlow<PagingData<Episode>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    val characterCount = repository.getCharacterCount().asLiveData()

    val episodeCount = repository.getEpisodeCount().asLiveData()

    fun readCharacterById(id:String) = repository.getCharacterById(id)

    fun readEpisodeById(id:String) = repository.getEpisodeById(id)

    fun readCharactersData() =
        viewModelScope.launch {
            val result = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                repository.readAllCharactersData()
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope).value
            _charResult.value = result
        }

    fun readEpisodesData() =
        viewModelScope.launch {
            val result = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                repository.readAllEpisodesData()
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope).value
            _episodeResult.value = result
        }

    val readLimitedCharactersData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedCharactersData()
    }.flow.cachedIn(viewModelScope)

    val readLimitedEpisodesData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedEpisodeData()
    }.flow.cachedIn(viewModelScope)

    fun addCharacter(character: Character) = viewModelScope.launch {
        repository.addCharacter(character)
    }

    fun addEpisode(episode: Episode) = viewModelScope.launch {
        repository.addEpisode(episode)
    }

    val itemCounts = MediatorLiveData<Pair<Int?,Int?>>().apply {
        addSource(characterCount) {value = Pair(it, episodeCount.value)}
        addSource(episodeCount) {value = Pair(characterCount.value, it)}
    }
}