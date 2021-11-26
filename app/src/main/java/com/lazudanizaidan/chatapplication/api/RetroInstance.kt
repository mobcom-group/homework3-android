package com.lazudanizaidan.chatapplication.api

import com.lazudanizaidan.chatapplication.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api: Api by lazy {
        retrofit.create(Api::class.java)
    }
//    private val client = OkHttpClient.Builder().build()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(Constants.BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .build()
//
//    fun<T> buildService(service: Class<T>): T{
//        return retrofit.create(service)
//    }
}