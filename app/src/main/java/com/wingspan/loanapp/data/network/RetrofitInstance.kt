package com.wingspan.loanapp.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    // ---------- BACKEND RETROFIT ----------
    private var backendRetrofit: Retrofit? = null

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        if (backendRetrofit == null) {
            backendRetrofit = Retrofit.Builder()
                .baseUrl("https://loan-swift-backend.onrender.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return backendRetrofit!!
    }

    @Provides
    @Singleton
    fun provideApiService( retrofit: Retrofit): ApiServices =
        retrofit.create(ApiServices::class.java)
}