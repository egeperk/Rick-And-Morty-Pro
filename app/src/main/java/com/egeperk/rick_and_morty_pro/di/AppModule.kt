package com.egeperk.rick_and_morty_pro.di

import com.apollographql.apollo3.ApolloClient
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import com.egeperk.rick_and_morty_pro.util.Constants.BASE_URL
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    val repoModule = module {
        single { ApiRepository(get()) }
        single {
            ApolloClient.Builder().serverUrl(BASE_URL).build()
        }
    }

    val viewModeleModule = module {
        viewModel { HomeViewModel(get()) }
    }
}