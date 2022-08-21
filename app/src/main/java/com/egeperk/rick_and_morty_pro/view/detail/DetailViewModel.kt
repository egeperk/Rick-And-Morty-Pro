package com.egeperk.rick_and_morty_pro.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.CharacterDetailPagingSource
import com.egeperk.rick_and_morty_pro.adapters.pagingsource.EpisodeDetailPagingSource
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import com.egeperk.rick_and_morty_pro.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _character: MutableLiveData<CharacterByIdQuery.Character?> = MutableLiveData()
    val character: MutableLiveData<CharacterByIdQuery.Character?> = _character

    private val _episode: MutableLiveData<EpisodeByIdQuery.Episode?> = MutableLiveData()
    val episode: MutableLiveData<EpisodeByIdQuery.Episode?> = _episode

    private val _episodeResult =
        MutableStateFlow<PagingData<CharacterByIdQuery.Episode>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    private val _characterResult =
        MutableStateFlow<PagingData<EpisodeByIdQuery.Character>>(PagingData.empty())
    val characterResult = _characterResult.asStateFlow()

    fun getCharacterData(id: String) {
        viewModelScope.launch {
            _character.value = repository.characterByIdQuery(id).data?.character
        }
    }

    fun getEpisodeData(id: String) =
        viewModelScope.launch {
            _episode.value = repository.episodeByIdQuery(id).data?.episode
        }


    fun getCharacterEpisodes(id: String, showThree: Boolean) =
        viewModelScope.launch {
            val newResult = Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
                CharacterDetailPagingSource(
                    repository,
                    id,
                    showThree
                )
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
            _episodeResult.value = newResult.value
        }


    fun getEpisodeCharacters(id: String, showThree: Boolean) =
        viewModelScope.launch {
            val newResult = Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
                EpisodeDetailPagingSource(
                    repository,
                    id,
                    showThree
                )
            }.flow.cachedIn(viewModelScope).stateIn(viewModelScope)
            _characterResult.value = newResult.value
        }
}