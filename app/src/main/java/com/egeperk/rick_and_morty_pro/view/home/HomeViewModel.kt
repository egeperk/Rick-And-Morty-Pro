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
import com.egeperk.rick_and_morty_pro.util.Constants.EMPTY_VALUE
import com.egeperk.rick_and_morty_pro.util.Constants.PAGE_SIZE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ApiRepository): ViewModel() {

    val episodeCount = MutableLiveData<Int>()
    val charactersCount = MutableLiveData<Int>()

    val search = MutableStateFlow(EMPTY_VALUE)

    private val _charResult = MutableStateFlow<PagingData<CharactersQuery.Result>>(PagingData.empty())
    val charResult = _charResult.asStateFlow()

    private val _episodeResult = MutableStateFlow<PagingData<EpisodeQuery.Result>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    val charPosition = MutableLiveData<List<String>>()
    val episodePosition = MutableLiveData<List<String>>()

    var isSearch = MutableLiveData(false)

    fun getCharacterData(query: String, showFour: Boolean): StateFlow<PagingData<CharactersQuery.Result>> {

        viewModelScope.launch {

            charPosition.value = repository.charactersQuery(0,query).data?.characters?.results?.map { it?.id.toString() }

            val newResult = Pager(PagingConfig(pageSize = if (isSearch.value == true) PAGE_SIZE * 10 else PAGE_SIZE)) {
                CharacterHomePagingSource(repository, query, showFour)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)

            _charResult.value = newResult.value
            charactersCount.value = repository.charactersQuery(0, EMPTY_VALUE).data?.characters?.info?.count!!
        }
        return charResult
    }

    fun getEpisodeData(showFour: Boolean): StateFlow<PagingData<EpisodeQuery.Result>> {
        viewModelScope.launch{

            episodePosition.value = repository.episodesQuery(0).data?.episodes?.results?.map { it?.id.toString() }

            val newResult = Pager(PagingConfig(pageSize = PAGE_SIZE * 5)) {
                EpisodeHomePagingSource(repository, showFour)
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)

            _episodeResult.value = newResult.value
            episodeCount.value = repository.episodesQuery(0).data?.episodes?.info?.count!!
        }
        return episodeResult
    }
}