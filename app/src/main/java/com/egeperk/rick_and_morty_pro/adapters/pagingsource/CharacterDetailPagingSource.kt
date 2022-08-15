package com.egeperk.rick_and_morty_pro.adapters.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty_pro.repository.ApiRepository

class CharacterDetailPagingSource(
    private val repository: ApiRepository,
    private val id: String,
    private val size: Int
) :
    PagingSource<Int, CharacterByIdQuery.Episode>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterByIdQuery.Episode> {

        return try {
            val response = repository.characterByIdQuery(id)
            val data = response.data?.character?.episode
            val characters = mapResponseToPresentationModel(data!!)
            if (!response.hasErrors()) {
                LoadResult.Page(
                    data = if (size == 0) characters.subList(0, 3) else characters,
                    nextKey = null,
                    prevKey = null
                )
            } else {
                LoadResult.Error(Exception(response.errors?.first()?.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun mapResponseToPresentationModel(results: List<CharacterByIdQuery.Episode?>): List<CharacterByIdQuery.Episode> {
        val episodes = mutableListOf<CharacterByIdQuery.Episode>()
        for (result in results) {
            val episodeName = result?.name
            val episodeAirDate = result?.air_date
            val episodeNumber = result?.episode
            episodes.add(
                CharacterByIdQuery.Episode(
                    episodeName,
                    episodeAirDate,
                    episodeNumber
                )
            )
        }
        return episodes
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterByIdQuery.Episode>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}