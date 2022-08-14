package com.egeperk.rick_and_morty_pro

import android.app.Application
import android.content.Context
import com.egeperk.rick_and_morty_pro.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RickAndMortyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RickAndMortyApp)
            modules(
                AppModule.repoModule,
                AppModule.viewModelModule)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}