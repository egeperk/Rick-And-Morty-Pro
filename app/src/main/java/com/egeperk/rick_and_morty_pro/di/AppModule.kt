package com.egeperk.rick_and_morty_pro.di

import android.app.Application
import androidx.room.Room
import com.apollographql.apollo3.ApolloClient
import com.egeperk.rick_and_morty_pro.data.db.ItemDao
import com.egeperk.rick_and_morty_pro.data.db.ItemDatabase
import com.egeperk.rick_and_morty_pro.data.repository.LocalRepository
import com.egeperk.rick_and_morty_pro.repository.ApiRepository
import com.egeperk.rick_and_morty_pro.util.Constants.BASE_URL
import com.egeperk.rick_and_morty_pro.view.detail.DetailViewModel
import com.egeperk.rick_and_morty_pro.view.favorites.FavoritesViewModel
import com.egeperk.rick_and_morty_pro.view.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    val roomModule = module {
        single {
            Room.databaseBuilder(
                androidApplication(),
                ItemDatabase::class.java,
                "rickmorty_db.db"
            ).build()
        }
        single { get<ItemDatabase>().itemDao() }
    }

    val repoModule = module {
        single { ApiRepository(get()) }
        single {
            ApolloClient.Builder().serverUrl(BASE_URL).build()
        }
        single { LocalRepository(get()) }
    }

    val viewModelModule = module {
        viewModel { HomeViewModel(get()) }
        viewModel { DetailViewModel(get()) }
        viewModel { FavoritesViewModel(get()) }
    }


}