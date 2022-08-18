package com.egeperk.rick_and_morty_pro.adapters.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty_pro.repository.ApiRepository

class CharacterHomePagingSource(private val repository: ApiRepository, private val query: String, private val size: Int) :
    PagingSource<Int, CharactersQuery.Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharactersQuery.Result> {

        return try {
            val page = params.key ?: 1
            val response = repository.charactersQuery(page, query)
            val nextKey = response.data?.characters?.info?.next
            val data = response.data?.characters?.results
            val characters = mapResponseToPresentationModel(data!!)
            if (!response.hasErrors()) {
                LoadResult.Page(
                    data = if(size == 0) characters.slice(0..3) else characters,
                    nextKey = if (size == 0) null else nextKey,
                    prevKey = null)
            } else {
                LoadResult.Error(Exception(response.errors?.first()?.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun mapResponseToPresentationModel(results: List<CharactersQuery.Result?>): List<CharactersQuery.Result> {
        val characters = mutableListOf<CharactersQuery.Result>()
        for (result in results) {
            val characterId = result?.id
            val characterImage = result?.image
            val characterName = result?.name
            characters.add(CharactersQuery.Result(
                characterId,
                characterName,
                characterImage
             ))
        }
        return characters
    }

    override fun getRefreshKey(state: PagingState<Int, CharactersQuery.Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}