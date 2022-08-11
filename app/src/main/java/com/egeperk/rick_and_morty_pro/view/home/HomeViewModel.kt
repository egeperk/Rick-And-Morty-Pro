package com.egeperk.rick_and_morty_pro.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.CharacterHomePagingSource
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.EpisodeHomePagingSource
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import com.egeperk.rick_and_morty_pro.util.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ApiRepository): ViewModel() {

    val episodeCount = MutableLiveData<Int>()
    val charactersCount = MutableLiveData<Int>()

    private val _charResult = MutableStateFlow<PagingData<CharactersQuery.Result>>(PagingData.empty())
    val charResult = _charResult.asStateFlow()

    private val _episodeResult = MutableStateFlow<PagingData<EpisodeQuery.Result>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    fun getCharacterData(query: String): StateFlow<PagingData<CharactersQuery.Result>> {

        viewModelScope.launch {
            val newResult = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                CharacterHomePagingSource(repository, query)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
            _charResult.value = newResult.value
            charactersCount.value = repository.charactersQuery(0,"").data?.characters?.info?.count!!
        }
        return charResult
    }

    fun getEpisodeData(): StateFlow<PagingData<EpisodeQuery.Result>> {

        viewModelScope.launch {
            val newResult = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                EpisodeHomePagingSource(repository)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
            _episodeResult.value = newResult.value
            episodeCount.value = repository.episodesQuery(0).data?.episodes?.info?.count!!
        }
        return episodeResult
    }


    init {
        getCharacterData("")
        getEpisodeData()
    }

}