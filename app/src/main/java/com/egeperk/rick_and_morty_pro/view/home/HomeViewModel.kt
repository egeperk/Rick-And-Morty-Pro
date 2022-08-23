package com.egeperk.rick_and_morty_pro.view.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.CharacterHomePagingSource
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.EpisodeHomePagingSource
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.SearchCharacterPagingSource
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ApiRepository) : ViewModel() {

    val episodeCount = MutableLiveData<Int?>()
    val charactersCount = MutableLiveData<Int?>()

    val search = MutableStateFlow(EMPTY_VALUE)

    private val _charResult =
        MutableStateFlow<PagingData<CharactersQuery.Result>>(PagingData.empty())
    val charResult = _charResult.asStateFlow()

    private val _charSearchResult =
        MutableStateFlow<PagingData<CharactersQuery.Result>>(PagingData.empty())
    val charSearchResult = _charSearchResult.asStateFlow()

    private val _episodeResult =
        MutableStateFlow<PagingData<EpisodeQuery.Result>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    var isSearch = MutableLiveData(false)

    fun getCharacterData(
        query: String,
        showFour: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newResult =
                Pager(PagingConfig(pageSize = if (isSearch.value == true) PAGE_SIZE * 10 else PAGE_SIZE)) {
                    CharacterHomePagingSource(repository, query, showFour)
                }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)

            _charResult.value = newResult.value
        }
    }

    fun searchCharacterData(query: String) {

        viewModelScope.launch(Dispatchers.IO) {

            val newResult = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                SearchCharacterPagingSource(repository, query)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)

            _charSearchResult.value = newResult.value
        }
    }

    fun getEpisodeData(
        showFour: Boolean,
        name: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val newResult = Pager(PagingConfig(pageSize = PAGE_SIZE * 5)) {
                EpisodeHomePagingSource(repository, showFour, name)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
            _episodeResult.value = newResult.value
        }
    }

    fun getEpisodeCount(name: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        episodeCount.postValue(repository.episodesQuery(0, name).data?.episodes?.info?.count)
    }

    fun getCharacterCount(query: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        charactersCount.postValue(repository.charactersQuery(0, query).data?.characters?.info?.count)
    }
}