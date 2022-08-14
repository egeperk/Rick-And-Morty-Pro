package com.egeperk.rick_and_morty_pro.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.egeperk.rick_and_morty.CharacterByIdQuery
import com.egeperk.rick_and_morty.CharactersQuery
import com.egeperk.rick_and_morty.EpisodeQuery
import com.egeperk.rick_and_morty.type.Episode

class ApiRepository(private val api: ApolloClient) {

    suspend fun charactersQuery(page: Int?, query: String?): ApolloResponse<CharactersQuery.Data> {
        return api.query(CharactersQuery(Optional.Present(page), Optional.Present(query))).execute()
    }

    suspend fun episodesQuery(page: Int?): ApolloResponse<EpisodeQuery.Data> {
        return api.query(EpisodeQuery(Optional.Present(page))).execute()
    }

    suspend fun characterByIdQuery(id: String) : ApolloResponse<CharacterByIdQuery.Data> {
        return api.query(CharacterByIdQuery(id)).execute()
    }

}