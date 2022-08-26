package com.egeperk.rick_and_morty_pro.view.favorites

import androidx.lifecycle.*
import androidx.paging.*
import com.egeperk.rick_and_morty_pro.data.model.Character
import com.egeperk.rick_and_morty_pro.data.model.Episode
import com.egeperk.rick_and_morty_pro.data.repository.LocalRepository
import com.egeperk.rick_and_morty_pro.util.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: LocalRepository) :
    ViewModel() {

    private val _character: MutableStateFlow<Character>? = null
    val character = _character?.asStateFlow()

    private val _episodeResult = MutableStateFlow<PagingData<Episode>>(PagingData.empty())
    val episodeResult = _episodeResult.asStateFlow()

    val characterCount = repository.getCharacterCount().asLiveData()

    val episodeCount = repository.getEpisodeCount().asLiveData()

    fun readCharacterById(id: String) = repository.getCharacterById(id)

    fun readEpisodeById(id: String) = repository.getEpisodeById(id)

    val characters = repository.readAllCharactersData()

    fun readEpisodesData() =
        viewModelScope.launch(Dispatchers.IO) {
           val result = repository.readAllEpisodesData().map {
                it.map { data ->
                    Episode(
                        id = data.id,
                        name = data.name,
                        episode = data.episode,
                        air_date = data.air_date,
                        pk = data.id?.toInt() ?: 0
                    )
                }
            }.stateIn(viewModelScope).value
            _episodeResult.value = PagingData.from(result)
        }

    val episodes = repository.readAllEpisodesData().asLiveData()

    val readLimitedCharactersData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedCharactersData()
    }.flow.map {
        it.map { data ->
            Character(
                id = data.id,
                name = data.name,
                image = data.image,
                status = data.status,
                gender = data.gender,
                species = data.species,
                type = data.type,
                origin = data.origin,
                location = data.location,
                pk = data.id?.toInt() ?: 0
            )
        }
    }.cachedIn(viewModelScope)

    val readLimitedEpisodesData = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        repository.readLimitedEpisodeData()
    }.flow.map {
        it.map { data ->
            Episode(
                id = data.id,
                name = data.name,
                episode = data.episode,
                air_date = data.air_date,
                pk = data.id?.toInt() ?: 0
            )
        }
    }.cachedIn(viewModelScope)

    fun addCharacter(character: Character) = viewModelScope.launch(Dispatchers.IO) {
        repository.addCharacter(character)
    }

    fun addEpisode(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        repository.addEpisode(episode)
    }

}