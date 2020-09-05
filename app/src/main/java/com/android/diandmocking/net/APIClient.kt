package com.android.diandmocking.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient(private val baseUrl: String, private val isAuthorizationRequired: Boolean) {

    var httpClientBuilder = OkHttpClient.Builder()
    var adapterBuilder = Retrofit.Builder()

    init {
        createAdapter()
    }

    private fun createAdapter() {
        if (BuildConfig.DEBUG) {
            val httpLogInterceptor = HttpLoggingInterceptor()
            httpLogInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(httpLogInterceptor)
        }
        adapterBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
        if (isAuthorizationRequired) {
            httpClientBuilder.addInterceptor(AuthorizationInterceptor())
        } else {
            httpClientBuilder.addInterceptor(BasicInterceptor())
        }
    }

    fun <S> createService(serviceClass: Class<S>): S {
        return adapterBuilder
            .client(httpClientBuilder.build())
            .build()
            .create(serviceClass)
    }
}