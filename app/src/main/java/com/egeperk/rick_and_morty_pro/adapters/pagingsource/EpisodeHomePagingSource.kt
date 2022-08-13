package com.egeperk.rick_and_morty_pro.adapters.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import java.text.FieldPosition

class EpisodeHomePagingSource(private val repository: ApiRepository, private val size: Int ) :
    PagingSource<Int, EpisodeQuery.Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EpisodeQuery.Result> {

        return try {
            val nextPageNumber = params.key ?: 1
            val response = repository.episodesQuery(nextPageNumber)
            val nextKey = response.data?.episodes?.info?.next
            val data = response.data?.episodes?.results
            val episodes = mapResponseToPresentationModel(data!!)
            if (!response.hasErrors()) {
                LoadResult.Page(
                    data = if(size == 0) episodes.subList(0,4) else episodes,
                    nextKey = if (size == 0) null else nextKey,
                    prevKey = null)
            } else {
                LoadResult.Error(Exception(response.errors?.first()?.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun mapResponseToPresentationModel(results: List<EpisodeQuery.Result?>): List<EpisodeQuery.Result> {
        val episodes = mutableListOf<EpisodeQuery.Result>()
        for (result in results) {
            val episodeName = result?.episode
            val episodeAirDate = result?.air_date
            val episodeTitle = result?.name
            episodes.add(EpisodeQuery.Result(
                episodeName,episodeTitle,episodeAirDate

             ))
        }
        return episodes
    }

    override fun getRefreshKey(state: PagingState<Int, EpisodeQuery.Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}