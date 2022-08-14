package com.egeperk.rick_and_morty_pro.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _character : MutableLiveData<CharacterByIdQuery.Character?> = MutableLiveData()
    val character: MutableLiveData<CharacterByIdQuery.Character?> = _character

    fun getCharacterData(id: String): MutableLiveData<CharacterByIdQuery.Character?> {
        viewModelScope.launch {
            val data = repository.characterByIdQuery(id).data?.character
            _character.value = data
        }
        return character
    }
}