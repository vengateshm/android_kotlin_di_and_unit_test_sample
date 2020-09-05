package com.android.diandmocking

import android.app.Application
import com.android.diandmocking.di.domainModules
import com.android.diandmocking.di.repo
import com.android.diandmocking.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(viewModelModules, domainModules, repo))
        }
        super.onCreate()
    }
}