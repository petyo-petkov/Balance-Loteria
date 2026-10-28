package com.example.pruebas

import android.app.Application
import com.example.pruebas.di.databaseModule
import com.example.pruebas.di.networkModule
import com.example.pruebas.di.repositoryModule
import com.example.pruebas.di.scannerModule
import com.example.pruebas.di.useCaseModule
import com.example.pruebas.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                scannerModule,
                useCaseModule,
                viewModelModule,
                repositoryModule,
                databaseModule,
                networkModule
            )
        }
    }
}