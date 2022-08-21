package com.egeperk.rick_and_morty_pro.adapters.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egeperk.rick_and_morty.EpisodeByIdQuery
import com.egeperk.rick_and_morty_pro.repository.ApiRepository

class EpisodeDetailPagingSource(
    private val repository: ApiRepository,
    private val id: String,
    private val showFour: Boolean = true
) :
    PagingSource<Int, EpisodeByIdQuery.Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeByIdQuery.Character> {

        return try {
            val response = repository.episodeByIdQuery(id)
            val data = response.data?.episode?.characters
            val characters = mapResponseToPresentationModel(data!!)
            if (!response.hasErrors()) {
                LoadResult.Page(
                    data = if (showFour) {
                        if (characters.size < 4)
                            characters else characters.subList(0, 4)
                    } else
                        characters,
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

    private fun mapResponseToPresentationModel(results: List<EpisodeByIdQuery.Character?>): List<EpisodeByIdQuery.Character> {
        val characters = mutableListOf<EpisodeByIdQuery.Character>()
        for (result in results) {
            val characterId = result?.id
            val characterName = result?.name
            val characterImage = result?.image
            val characterLocation = result?.location
            characters.add(
                EpisodeByIdQuery.Character(
                    characterId,
                    characterName,
                    characterImage,
                    characterLocation
                )
            )
        }
        return characters
    }

    override fun getRefreshKey(state: PagingState<Int, EpisodeByIdQuery.Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}